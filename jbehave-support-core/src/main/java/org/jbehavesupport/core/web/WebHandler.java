package org.jbehavesupport.core.web;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.assertj.core.api.Assertions;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.ScenarioType;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.jbehavesupport.core.internal.web.GivenStoryHelper;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierNames;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.prependIfMissing;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.assertMandatoryColumns;
import static org.jbehavesupport.core.web.WebScreenshotType.*;
import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeanOfType;

@RequiredArgsConstructor
public class WebHandler {
    private static final String ACTION = "action";
    private static final String ELEMENT = "element";
    private static final String PROPERTY = "property";

    private static final ThreadLocal<WebSetting> CURRENT_SETTING = new ThreadLocal<>();

    private final ConfigurableListableBeanFactory beanFactory;
    private final TestContext testContext;
    private final WebDriver driver;
    private final WebActionResolver actionResolver;
    private final WebPropertyResolver propertyResolver;
    private final WebWaitConditionResolver waitConditionResolver;
    private final VerifierResolver verifierResolver;
    private final GivenStoryHelper givenStoryHelper;
    private final WebElementRegistry elementRegistry;
    private final WebDriverFactoryResolver webDriverFactoryResolver;
    private final ApplicationEventPublisher applicationEventPublisher;

    public static WebSetting getCurrentSetting() {
        return CURRENT_SETTING.get();
    }

    @BeforeScenario
    public void beforeScenario() {
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    @AfterScenario(uponType = ScenarioType.ANY, uponOutcome = AfterScenario.Outcome.SUCCESS)
    public void afterSuccessScenario() {
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    @AfterScenario(uponType = ScenarioType.ANY, uponOutcome = AfterScenario.Outcome.FAILURE)
    public void afterFailedScenario() {
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, FAILED));
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    public void openUrl(ExpressionEvaluatingParameter<String> url) {
        openSimpleUrl(url.getValue());
    }

    public void openUrl(String application, ExpressionEvaluatingParameter<String> pathExpression) {
        String path = prependIfMissing(pathExpression.getValue().replace("//(?!:)", "/"), "/");
        String url = resolveHomePageUrl(application) + path;
        openSimpleUrl(url);
    }

    public void openUrl(String application, String path, ExamplesTable queryParameters) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(resolveHomePageUrl(application) + prependIfMissing(path, "/"));
        for (Parameters queryParameter : queryParameters.getRowsAsParameters()) {
            String queryParameterName = queryParameter.valueAs(ExampleTableConstraints.NAME, String.class);
            String queryParameterData = queryParameter.valueAs(ExampleTableConstraints.DATA, String.class);
            uriBuilder.addParameter(queryParameterName, queryParameterData);
        }
        openSimpleUrl(uriBuilder.build().toString());
    }

    public void openHomePage(String application) {
        openSimpleUrl(resolveHomePageUrl(application));
    }

    private void openSimpleUrl(String url){
        driver.get(url);
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void performActions(String page, ExamplesTable actionTable) {
        assertMandatoryColumns(actionTable, ELEMENT, ACTION);
        for (Row actionRow : actionTable.getRowsAsParameters()) {
            Map<String, String> actionValues = actionRow.values();

            WebAction action = actionResolver.resolveAction(actionValues.get(ACTION));

            WebActionContext actionContext = WebActionContext.builder()
                .page(page)
                .element(actionValues.get(ELEMENT))
                .data(actionValues.get(ExampleTableConstraints.DATA))
                .alias(actionValues.getOrDefault(ExampleTableConstraints.ALIAS, null))
                .build();

            action.perform(actionContext);
            applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, DEBUG));
        }
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void storePropertiesInContext(String page, ExamplesTable table) {
        assertMandatoryColumns(table, ELEMENT, PROPERTY, ExampleTableConstraints.ALIAS);

        for (Row row : table.getRowsAsParameters()) {
            Map<String, String> values = row.values();
            Object value = resolvePropertyValue(page, values);
            testContext.put(values.get(ExampleTableConstraints.ALIAS), value, MetadataUtil.userDefined());
        }
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void verifyProperties(String page, ExamplesTable table) {
        assertMandatoryColumns(table, ELEMENT, PROPERTY, ExampleTableConstraints.DATA);

        for (Row row : table.getRowsAsParameters()) {
            Map<String, String> values = row.values();

            Object expected = values.get(ExampleTableConstraints.DATA);
            Object actual = resolvePropertyValue(page, values);
            String verifierName = resolveVerifierName(values);

            Verifier verifier = verifierResolver.getVerifierByName(verifierName);
            verifier.verify(actual, expected);
        }
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void waitUntilCondition(String page, String element, ExpressionEvaluatingParameter<String> condition) {
        WebWaitConditionContext waitConditionCtx = WebWaitConditionContext.builder()
            .page(page)
            .element(element)
            .condition(condition.getValue())
            .value(parseConditionValue(condition.getValue()))
            .build();

        WebWaitCondition waitCondition = waitConditionResolver.resolveWaitCondition(waitConditionCtx);

        waitCondition.evaluate(waitConditionCtx);
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, WAIT));
    }

    /**
     * @deprecated(since = "1.0.7", forRemoval = true) because it's not opening new tab, only focus tab opened by another action
     *  getLastOpenedWindowHandler() may not work correctly on all browsers (https://developer.mozilla.org/en-US/docs/Web/WebDriver/Commands/GetWindowHandles)
     *  findTabWithUrlOrTitle() should be used instead
     */
    @Deprecated
    public void switchToNewTab() {
        assertThat(driver.getWindowHandles().size())
            .as("last tab remains, new was not opened")
            .isGreaterThan(0);
        driver.switchTo().window(getLastOpenedWindowHandler());
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void openAndFocusNewTab() {
        Set<String> handlesBefore = driver.getWindowHandles();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("window.open()");
            Set<String> handlesAfter = driver.getWindowHandles();
            if (handlesAfter.size() == handlesBefore.size() + 1) {
                handlesAfter.removeAll(handlesBefore);
                driver.switchTo().window(handlesAfter.iterator().next());
                applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
                return;
            }
        }
        throw new AssertionError("Opening new tab failed");
    }

    public void findTabWithUrlOrTitle(String urlTitle, ExpressionEvaluatingParameter<String> text) {
        Assertions.assertThat(urlTitle).matches("url|title").as("Must be url or title");
        driver.getWindowHandles().stream()
            .filter(handle -> {
                try {
                    driver.switchTo().window(handle);
                } catch (Exception e) {
                    return urlTitlecontainsText(urlTitle, text.getValue());
                }
                return urlTitlecontainsText(urlTitle, text.getValue());
            })
            .findAny()
            .orElseThrow(IllegalStateException::new);
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    private boolean urlTitlecontainsText(String urlTitle, String text) {
        return urlTitle.contains("url") ? driver.getCurrentUrl().contains(text) : driver.getTitle().contains(text);
    }

    public void focusNamedFrame(ExpressionEvaluatingParameter<String> page, ExpressionEvaluatingParameter<String> frame) {
        WebElement iFrame = driver.findElement(elementRegistry.getLocator(page.getValue(), frame.getValue()));
        driver.switchTo().frame(iFrame);
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void focusMainFrame() {
        driver.switchTo().defaultContent();
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void closeTab() {
        driver.close();
        driver.switchTo().window(getLastOpenedWindowHandler());
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void closeBrowser() {
        driver.quit();
    }

    public void navigateBack() {
        driver.navigate().back();
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void navigateForward() {
        driver.navigate().forward();
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, STEP));
    }

    public void takeScreenShot(){
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, MANUAL));
    }

    public void changeBrowser(String browserName) {
        driver.quit();
        webDriverFactoryResolver.setBrowserName(browserName);
    }

    private String parseConditionValue(String condition) {
        String[] conditionParts = condition.split(" ");
        return conditionParts.length >= 3 ? join(" ", asList(conditionParts).subList(2,conditionParts.length)) : null;
    }

    private Object resolvePropertyValue(String page, Map<String, String> row) {
        WebProperty property = propertyResolver.resolveProperty(row.get(PROPERTY));

        WebPropertyContext ctx = WebPropertyContext.builder()
            .page(page)
            .element(row.get(ELEMENT))
            .build();

        return property.value(ctx);
    }

    private String resolveVerifierName(Map<String, String> row) {
        String operator = VerifierNames.EQ;
        if (row.containsKey(ExampleTableConstraints.OPERATOR) && !row.get(ExampleTableConstraints.OPERATOR).isEmpty()) {
            operator = row.get(ExampleTableConstraints.OPERATOR);
        } else if (row.containsKey(ExampleTableConstraints.VERIFIER) && !row.get(ExampleTableConstraints.VERIFIER).isEmpty()) {
            operator = row.get(ExampleTableConstraints.VERIFIER);
        }
        return operator;
    }

    private String resolveHomePageUrl(String application) {
        WebSetting webSetting = qualifiedBeanOfType(beanFactory, WebSetting.class, application);
        CURRENT_SETTING.set(webSetting);
        return removeEnd(webSetting.getHomePageUrl(), "/");
    }

    private String getLastOpenedWindowHandler() {
        Set<String> handles = driver.getWindowHandles();
        assertThat(handles).as("no opened windows").isNotEmpty();
        return handles.stream().skip(handles.size() - 1).findFirst().get();
    }
}

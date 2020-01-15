package org.jbehavesupport.core.web;


import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;

import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.web.handlers.WebActionHandler;
import org.jbehavesupport.core.web.handlers.WebNavigationHandler;
import org.jbehavesupport.core.web.handlers.WebOthersHandler;
import org.jbehavesupport.core.web.handlers.WebPropertyHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class WebSteps {

    private final WebNavigationHandler webNavigationHandler;
    private final WebPropertyHandler webPropertyHandler;
    private final WebActionHandler webActionHandler;
    private final WebOthersHandler webOthersHandler;

    @Given("[$url] url is open")
    @When("[$url] url is open")
    public void openUrl(ExpressionEvaluatingParameter<String> url) {
        webNavigationHandler.openUrl(url);
    }

    @Given("[$application] homepage is open")
    @When("[$application] homepage is open")
    public void openHomePage(String application) {
        webNavigationHandler.openHomePage(application);
    }

    @Given(value = "[$application]/[$path] url is open", priority = 100)
    @When(value = "[$application]/[$path] url is open", priority = 100)
    public void openUrl(String application, ExpressionEvaluatingParameter<String> pathExpression) {
        webNavigationHandler.openUrl(application, pathExpression);
    }

    @Given("[$application]/[$path] url is open with query parameters:$queryParameters")
    @When("[$application]/[$path] url is open with query parameters:$queryParameters")
    public void openUrl(String application, String path, ExamplesTable queryParameters) throws URISyntaxException {
        webNavigationHandler.openUrl(application, path, queryParameters);
    }

    @When("on [$page] page these actions are performed:$actionTable")
    public void performActions(String page, ExamplesTable actionTable) {
        webActionHandler.performActions(page, actionTable);
    }

    @Given("on [$page] page these values are saved:$table")
    @Then("on [$page] page these values are saved:$table")
    public void storePropertiesInContext(String page, ExamplesTable table) {
        webPropertyHandler.storePropertiesInContext(page, table);
    }

    @Then("on [$page] page these conditions are verified:$table")
    public void verifyProperties(String page, ExamplesTable table) {
        webPropertyHandler.verifyProperties(page, table);
    }

    /**
     * Waits on given page, until requested condition is met, or timeout runs out.
     *
     * @param page      page defined in yml file
     * @param element   element specified under page in same yml file or reserved keyword: @url | @title
     * @param condition parsed for following expressions: <ul>
     *                  <li>is present</li>
     *                  <li>is clickable</li>
     *                  <li>is visible</li>
     *                  <li>is not visible</li>
     *                  <li>has $attr $attrVal (i.e. has text someText | has class issue | has id)</li>
     *                  <li>missing $attr $attrVal (i.e. missing text java | missing customAttributeName working | missing class)</li>
     *
     *                  </ul>
     */
    @Then("on [$page] page wait until [$element] $condition")
    public void waitUntilCondition(String page, String element, ExpressionEvaluatingParameter<String> condition) {
        webOthersHandler.waitUntilCondition(page, element, condition);
    }

    /**
     * @deprecated(since = "1.0.7", forRemoval = true) because it's not opening new tab, only focus tab opened by another action
     *  getLastOpenedWindowHandler() may not work correctly on all browsers (https://developer.mozilla.org/en-US/docs/Web/WebDriver/Commands/GetWindowHandles)
     *  findTabWithUrlOrTitle() should be used instead
     */
    @Deprecated
    @Then("new tab is opened and focused")
    public void switchToNewTab() {
        webNavigationHandler.switchToNewTab();
    }

    @Given("open and focus new tab")
    @Then("open and focus new tab")
    public void openAndFocusNewTab() {
        webNavigationHandler.openAndFocusNewTab();
    }

    @Given("tab with [$urlTitle] containing [$text] is focused")
    @Then("tab with [$urlTitle] containing [$text] is focused")
    public void findTabWithUrlOrTitle(String urlTitle, ExpressionEvaluatingParameter<String> text) {
        webNavigationHandler.findTabWithUrlOrTitle(urlTitle, text);
    }

    @Given("on page [$page] frame [$frame] is focused")
    @Then("on page [$page] frame [$frame] is focused")
    public void focusNamedFrame(ExpressionEvaluatingParameter<String> page, ExpressionEvaluatingParameter<String> frame) {
        webNavigationHandler.focusNamedFrame(page, frame);
    }

    @Given("main frame is focused")
    @Then("main frame is focused")
    public void focusMainFrame() {
        webNavigationHandler.focusMainFrame();
    }

    @When("current tab is closed")
    public void closeTab() {
        webNavigationHandler.closeTab();
    }

    @Given("browser is closed")
    public void closeBrowser() {
        webNavigationHandler.closeBrowser();
    }

    @When("navigated back")
    @Then("navigate back")
    public void navigateBack() {
        webNavigationHandler.navigateBack();
    }

    @When("navigated forward")
    @Then("navigate forward")
    public void navigateForward() {
        webNavigationHandler.navigateForward();
    }

    @When("screenshot is taken")
    @Then("screenshot is taken")
    public void takeScreenShot(){
        webOthersHandler.takeScreenShot();
    }

    @When("browser is changed to [$browserName]")
    @Given("browser is changed to [$browserName]")
    public void changeBrowser(String browserName) {
        webNavigationHandler.changeBrowser(browserName);
    }
}

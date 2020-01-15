package org.jbehavesupport.core.web.handlers;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.web.WebScreenshotEvent;
import org.jbehavesupport.core.web.WebWaitCondition;
import org.jbehavesupport.core.web.WebWaitConditionContext;
import org.jbehavesupport.core.web.WebWaitConditionResolver;
import org.springframework.context.ApplicationEventPublisher;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static org.jbehavesupport.core.web.WebScreenshotType.MANUAL;
import static org.jbehavesupport.core.web.WebScreenshotType.WAIT;

@RequiredArgsConstructor
public class WebOthersHandler extends WebAbstractHandler{

    private final WebWaitConditionResolver waitConditionResolver;
    private final ApplicationEventPublisher applicationEventPublisher;

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

    public void takeScreenShot(){
        applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, MANUAL));
    }

    private String parseConditionValue(String condition) {
        String[] conditionParts = condition.split(" ");
        return conditionParts.length >= 3 ? join(" ", asList(conditionParts).subList(2,conditionParts.length)) : null;
    }
}

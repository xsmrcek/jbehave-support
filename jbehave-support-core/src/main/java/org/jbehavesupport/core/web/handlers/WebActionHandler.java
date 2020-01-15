package org.jbehavesupport.core.web.handlers;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.web.WebAction;
import org.jbehavesupport.core.web.WebActionContext;
import org.jbehavesupport.core.web.WebActionResolver;
import org.jbehavesupport.core.web.WebScreenshotEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.jbehavesupport.core.internal.ExamplesTableUtil.assertMandatoryColumns;
import static org.jbehavesupport.core.web.WebScreenshotType.DEBUG;
import static org.jbehavesupport.core.web.WebScreenshotType.STEP;

@RequiredArgsConstructor
public class WebActionHandler extends WebAbstractHandler{

    //private static final String ACTION = "action";
    //private static final String ELEMENT = "element";

    private final WebActionResolver actionResolver;
    private final ApplicationEventPublisher applicationEventPublisher;

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
}

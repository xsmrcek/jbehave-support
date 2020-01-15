package org.jbehavesupport.core.web.handlers;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierNames;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.jbehavesupport.core.web.WebProperty;
import org.jbehavesupport.core.web.WebPropertyContext;
import org.jbehavesupport.core.web.WebPropertyResolver;
import org.jbehavesupport.core.web.WebScreenshotEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.jbehavesupport.core.internal.ExamplesTableUtil.assertMandatoryColumns;
import static org.jbehavesupport.core.web.WebScreenshotType.STEP;

@RequiredArgsConstructor
public class WebPropertyHandler extends WebAbstractHandler{

    //private static final String PROPERTY = "property";
    //private static final String ELEMENT = "element";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerifierResolver verifierResolver;
    private final TestContext testContext;
    private final WebPropertyResolver propertyResolver;

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

    private String resolveVerifierName(Map<String, String> row) {
        String operator = VerifierNames.EQ;
        if (row.containsKey(ExampleTableConstraints.OPERATOR) && !row.get(ExampleTableConstraints.OPERATOR).isEmpty()) {
            operator = row.get(ExampleTableConstraints.OPERATOR);
        } else if (row.containsKey(ExampleTableConstraints.VERIFIER) && !row.get(ExampleTableConstraints.VERIFIER).isEmpty()) {
            operator = row.get(ExampleTableConstraints.VERIFIER);
        }
        return operator;
    }

    private Object resolvePropertyValue(String page, Map<String, String> row) {
        WebProperty property = propertyResolver.resolveProperty(row.get(PROPERTY));

        WebPropertyContext ctx = WebPropertyContext.builder()
            .page(page)
            .element(row.get(ELEMENT))
            .build();

        return property.value(ctx);
    }
}

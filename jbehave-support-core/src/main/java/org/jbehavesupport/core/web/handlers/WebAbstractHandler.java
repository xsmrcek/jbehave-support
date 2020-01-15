package org.jbehavesupport.core.web.handlers;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@NoArgsConstructor
public class WebAbstractHandler {
    public static final String ACTION = "action";
    public static final String ELEMENT = "element";
    public static final String PROPERTY = "property";

    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;
}

package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceStartFailureEvent extends TemplateInstanceEvent {

    private final Throwable throwable;

    public TemplateInstanceStartFailureEvent(TemplateInstance instance, Throwable throwable) {
        super(instance);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateStartFailureEvent extends InstanceTemplateEvent {

    private final Throwable throwable;

    public InstanceTemplateStartFailureEvent(InstanceTemplate instance, Throwable throwable) {
        super(instance);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

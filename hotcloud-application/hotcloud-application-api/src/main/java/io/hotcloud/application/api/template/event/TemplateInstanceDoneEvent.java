package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceDoneEvent extends TemplateInstanceEvent {

    private final boolean success;

    public TemplateInstanceDoneEvent(TemplateInstance instance, boolean success) {
        super(instance);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

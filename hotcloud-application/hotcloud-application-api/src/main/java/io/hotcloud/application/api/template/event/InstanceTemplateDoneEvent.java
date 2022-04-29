package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateDoneEvent extends InstanceTemplateEvent {

    private final boolean success;

    public InstanceTemplateDoneEvent(InstanceTemplate instance, boolean success) {
        super(instance);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

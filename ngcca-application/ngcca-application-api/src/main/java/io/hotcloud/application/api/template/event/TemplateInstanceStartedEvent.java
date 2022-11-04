package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceStartedEvent extends TemplateInstanceEvent {

    public TemplateInstanceStartedEvent(TemplateInstance instance) {
        super(instance);
    }
}

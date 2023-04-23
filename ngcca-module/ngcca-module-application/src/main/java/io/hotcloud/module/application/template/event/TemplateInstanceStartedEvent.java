package io.hotcloud.module.application.template.event;


import io.hotcloud.module.application.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceStartedEvent extends TemplateInstanceEvent {

    public TemplateInstanceStartedEvent(TemplateInstance instance) {
        super(instance);
    }
}

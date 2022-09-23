package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceDeleteEvent extends TemplateInstanceEvent {

    public TemplateInstanceDeleteEvent(TemplateInstance instance) {
        super(instance);
    }
}

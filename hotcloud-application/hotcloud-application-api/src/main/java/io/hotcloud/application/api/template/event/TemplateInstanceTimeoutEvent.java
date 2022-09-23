package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.TemplateInstance;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateInstanceTimeoutEvent extends TemplateInstanceEvent {

    public TemplateInstanceTimeoutEvent(TemplateInstance instance) {
        super(instance);
    }

}

package io.hotcloud.module.application.template.event;

import io.hotcloud.module.application.template.TemplateInstance;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class TemplateInstanceEvent extends ApplicationEvent {

    public TemplateInstanceEvent(TemplateInstance instance) {
        super(instance);
    }

    public TemplateInstance getInstance() {
        return ((TemplateInstance) super.getSource());
    }
}

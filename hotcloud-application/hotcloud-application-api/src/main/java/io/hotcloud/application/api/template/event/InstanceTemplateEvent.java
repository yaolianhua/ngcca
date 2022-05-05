package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.InstanceTemplate;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class InstanceTemplateEvent extends ApplicationEvent {

    public InstanceTemplateEvent(InstanceTemplate instance) {
        super(instance);
    }

    public InstanceTemplate getInstance() {
        return ((InstanceTemplate) super.getSource());
    }
}

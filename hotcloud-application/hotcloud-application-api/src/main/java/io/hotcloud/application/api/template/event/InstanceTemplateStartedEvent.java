package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateStartedEvent extends InstanceTemplateEvent {

    public InstanceTemplateStartedEvent(InstanceTemplate instance) {
        super(instance);
    }
}

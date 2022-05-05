package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.template.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateDeleteEvent extends InstanceTemplateEvent {

    public InstanceTemplateDeleteEvent(InstanceTemplate instance) {
        super(instance);
    }
}

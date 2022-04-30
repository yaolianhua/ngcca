package io.hotcloud.application.api.template.event;

import io.hotcloud.application.api.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateTimeoutEvent extends InstanceTemplateEvent {

    public InstanceTemplateTimeoutEvent(InstanceTemplate instance) {
        super(instance);
    }

}

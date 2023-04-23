package io.hotcloud.module.application.core.event;

import io.hotcloud.module.application.core.ApplicationInstance;
import org.springframework.context.ApplicationEvent;

public abstract class ApplicationInstanceEvent extends ApplicationEvent {

    public ApplicationInstanceEvent(ApplicationInstance instance) {
        super(instance);
    }

    public ApplicationInstance getInstance() {
        return ((ApplicationInstance) super.getSource());
    }
}

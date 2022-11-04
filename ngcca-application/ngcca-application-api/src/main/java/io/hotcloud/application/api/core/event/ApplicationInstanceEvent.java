package io.hotcloud.application.api.core.event;

import io.hotcloud.application.api.core.ApplicationInstance;
import org.springframework.context.ApplicationEvent;

public abstract class ApplicationInstanceEvent extends ApplicationEvent {

    public ApplicationInstanceEvent(ApplicationInstance instance) {
        super(instance);
    }

    public ApplicationInstance getInstance() {
        return ((ApplicationInstance) super.getSource());
    }
}

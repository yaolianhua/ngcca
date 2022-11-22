package io.hotcloud.application.api.core.event;

import io.hotcloud.application.api.core.ApplicationInstance;

public class ApplicationInstanceCreateEvent extends ApplicationInstanceEvent{

    public ApplicationInstanceCreateEvent(ApplicationInstance instance) {
        super(instance);
    }
}

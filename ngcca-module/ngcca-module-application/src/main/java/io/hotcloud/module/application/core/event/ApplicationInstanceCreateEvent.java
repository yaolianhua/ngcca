package io.hotcloud.module.application.core.event;


import io.hotcloud.module.application.core.ApplicationInstance;

public class ApplicationInstanceCreateEvent extends ApplicationInstanceEvent {

    public ApplicationInstanceCreateEvent(ApplicationInstance instance) {
        super(instance);
    }
}

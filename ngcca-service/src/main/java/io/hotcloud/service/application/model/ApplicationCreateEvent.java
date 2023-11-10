package io.hotcloud.service.application.model;

import org.springframework.context.ApplicationEvent;

public class ApplicationCreateEvent extends ApplicationEvent {

    private final ApplicationInstance instance;

    public ApplicationCreateEvent(Object source) {
        super(source);
        this.instance = (ApplicationInstance) source;
    }

    public ApplicationInstance getInstance() {
        return instance;
    }
}

package io.hotcloud.service.application;

import io.hotcloud.service.application.model.ApplicationCreateEvent;
import io.hotcloud.service.application.processor.ApplicationInstanceProcessors;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener {

    private final ApplicationInstanceProcessors applicationInstanceProcessors;

    public ApplicationEventListener(ApplicationInstanceProcessors applicationInstanceProcessors) {
        this.applicationInstanceProcessors = applicationInstanceProcessors;
    }

    @EventListener(value = ApplicationCreateEvent.class)
    @Async
    public void create(ApplicationCreateEvent event) {
        applicationInstanceProcessors.processCreate(event.getInstance());
    }

}

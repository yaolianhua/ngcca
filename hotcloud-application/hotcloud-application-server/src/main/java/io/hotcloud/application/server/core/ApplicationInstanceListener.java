package io.hotcloud.application.server.core;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.core.event.ApplicationInstanceCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = ApplicationProperties.PROPERTIES_TYPE_NAME,
        havingValue = ApplicationProperties.IN_PROCESS,
        matchIfMissing = true
)
@RequiredArgsConstructor
public class ApplicationInstanceListener {

    private final ApplicationInstanceK8sService applicationInstanceK8sService;

    @EventListener
    @Async
    public void applicationInstanceCreate (ApplicationInstanceCreateEvent createEvent){
        applicationInstanceK8sService.processApplicationCreatedBlocked(createEvent.getInstance());
    }
}

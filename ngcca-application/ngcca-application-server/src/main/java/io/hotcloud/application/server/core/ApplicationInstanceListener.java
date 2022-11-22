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

    private final ApplicationDeploymentWatchService applicationDeploymentWatchService;

    @EventListener
    @Async
    public void applicationInstanceCreate (ApplicationInstanceCreateEvent createEvent){
        applicationDeploymentWatchService.inProcessWatch(createEvent.getInstance());
    }
}

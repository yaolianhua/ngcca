package io.hotcloud.server.buildpack.service;

import io.hotcloud.vendor.buildpack.event.BuildPackStartedEventV2;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = BuildPackImagebuildProperties.PROPERTIES_TYPE_NAME,
        havingValue = BuildPackImagebuildProperties.IN_PROCESS,
        matchIfMissing = true
)
public class BuildPackListenerV2 {
    private final BuildPackJobWatchService buildPackJobWatchService;

    @Async
    @EventListener
    public void started(BuildPackStartedEventV2 startedEvent) {
        buildPackJobWatchService.inProcessWatch(startedEvent.getBuildPack());
    }

}

package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEventV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildPackListenerV2 {

    private final BuildPackService buildPackService;

    @Async
    @EventListener
    public void started(BuildPackStartedEventV2 startedEvent) {

    }

}

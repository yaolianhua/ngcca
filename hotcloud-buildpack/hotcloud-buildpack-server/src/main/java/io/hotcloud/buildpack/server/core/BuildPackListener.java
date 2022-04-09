package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackListener {

    @Async
    @EventListener
    public void started(BuildPackStartedEvent startedEvent) {

    }

    @Async
    @EventListener
    public void started(BuildPackStartFailureEvent startFailureEvent) {

    }
}

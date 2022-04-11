package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.buildpack.api.core.model.DefaultBuildPack;
import io.hotcloud.common.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackListener {

    private final BuildPackService buildPackService;

    public BuildPackListener(BuildPackService buildPackService) {
        this.buildPackService = buildPackService;
    }

    @Async
    @EventListener
    public void started(BuildPackStartedEvent startedEvent) {
        log.info("BuildPackListener. {}", startedEvent);
    }

    @Async
    @EventListener
    public void startFailure(BuildPackStartFailureEvent startFailureEvent) {
        try {
            DefaultBuildPack buildPack = (DefaultBuildPack) startFailureEvent.getBuildPack();
            Throwable throwable = startFailureEvent.getThrowable();
            String message = StringUtils.hasText(throwable.getMessage()) ? throwable.getMessage() : throwable.getCause().getMessage();

            buildPack.setDone(true);
            buildPack.setMessage(message);

            Assert.hasText(buildPack.getId(), "BuildPack ID is null");
            BuildPack saveOrUpdate = buildPackService.saveOrUpdate(buildPack);
            log.info("[BuildPackStartFailureEvent]. save or update buildPack [{}]", saveOrUpdate.getId());
        } catch (Throwable e) {
            log.error("[BuildPackStartFailureEvent] error. {}", e.getMessage(), e);
        }
    }
}

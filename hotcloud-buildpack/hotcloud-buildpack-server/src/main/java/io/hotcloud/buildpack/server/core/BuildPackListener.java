package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.buildpack.api.core.model.DefaultBuildPack;
import io.hotcloud.common.Assert;
import io.hotcloud.common.file.FileState;
import io.hotcloud.kubernetes.api.pod.PodApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackListener {

    private final BuildPackService buildPackService;
    private final ExecutorService executorService;
    private final PodApi podApi;

    public BuildPackListener(BuildPackService buildPackService,
                             ExecutorService executorService,
                             PodApi podApi) {
        this.buildPackService = buildPackService;
        this.executorService = executorService;
        this.podApi = podApi;
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async
    @EventListener
    public void started(BuildPackStartedEvent startedEvent) {
        DefaultBuildPack buildPack = ((DefaultBuildPack) startedEvent.getBuildPack());
        String clonedPath = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
        String tarball = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

        final CountDownLatch latch = new CountDownLatch(1);
        try {
            FileState fileState = new FileState(Path.of(clonedPath, tarball));

            executorService.submit(() -> {
                while (true) {
                    boolean waitCompleted = fileState.waitCompleted();
                    if (waitCompleted) {
                        latch.countDown();
                        break;
                    }
                }
            });

            PodList read = podApi.read(buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getLabels());
            Pod pod = read.getItems().get(0);

            while (latch.getCount() != 0) {
                sleep(10);
                try {
                    String logs = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName());
                    buildPack.setLogs(logs);
                    Assert.hasText(buildPack.getId(), "BuildPack ID is null");
                    buildPackService.saveOrUpdate(buildPack);
                } catch (Exception e) {
                    log.warn("[BuildPackStartedEvent] update buildPack logs error: {}", e.getMessage());
                }

            }

            sleep(10);
            String logs = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName());
            String lastLine = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName(), 1);
            buildPack.setDone(true);
            buildPack.setMessage(lastLine);
            buildPack.setLogs(logs);

            Assert.hasText(buildPack.getId(), "BuildPack ID is null");
            BuildPack saveOrUpdate = buildPackService.saveOrUpdate(buildPack);
            log.info("[BuildPackStartedEvent] update buildPack done [{}]", saveOrUpdate.getId());


        } catch (Exception e) {
            log.error("[BuildPackStartedEvent] error: {}", e.getMessage(), e);
        }

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
            log.info("[BuildPackStartFailureEvent] update buildPack [{}]", saveOrUpdate.getId());
        } catch (Throwable e) {
            log.error("[BuildPackStartFailureEvent] error {}", e.getMessage(), e);
        }
    }
}

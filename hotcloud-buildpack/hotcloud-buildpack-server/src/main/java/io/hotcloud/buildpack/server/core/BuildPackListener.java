package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.buildpack.api.core.model.DefaultBuildPack;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.api.workload.JobApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackListener {

    private final BuildPackService buildPackService;
    private final PodApi podApi;
    private final JobApi jobApi;

    public BuildPackListener(BuildPackService buildPackService,
                             PodApi podApi,
                             JobApi jobApi) {
        this.buildPackService = buildPackService;
        this.podApi = podApi;
        this.jobApi = jobApi;
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

        try {

            while (true) {
                sleep(15);
                Job job = jobApi.read(buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getName());
                BuildPackStatus.JobStatus jobStatus = BuildPackStatus.status(job);

                if (jobStatus == BuildPackStatus.JobStatus.Active) {
                    log.debug("[{}] user's BuildPack [{}] is not done yet!", buildPack.getUser(), buildPack.getId());
                }

                if (jobStatus == BuildPackStatus.JobStatus.Ready) {
                    log.debug("[{}] user's BuildPack [{}] is ready", buildPack.getUser(), buildPack.getId());
                }

                if (jobStatus == BuildPackStatus.JobStatus.Succeeded || jobStatus == BuildPackStatus.JobStatus.Failed) {
                    PodList read = podApi.read(buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getLabels());
                    Pod pod = read.getItems().get(0);

                    String logs = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName());
                    buildPack.setDone(true);
                    buildPack.setMessage(jobStatus == BuildPackStatus.JobStatus.Succeeded ? "BuildPack Success!" : "BuildPack Failed!");
                    buildPack.setLogs(logs);

                    Assert.hasText(buildPack.getId(), "BuildPack ID is null");
                    BuildPack saveOrUpdate = buildPackService.saveOrUpdate(buildPack);
                    log.info("[BuildPackStartedEvent] update buildPack done [{}]", saveOrUpdate.getId());
                    break;
                }
            }

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

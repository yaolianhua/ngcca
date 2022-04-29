package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackDeletedEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackDoneEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageBroadcaster;
import io.hotcloud.kubernetes.api.configurations.SecretApi;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimApi;
import io.hotcloud.kubernetes.api.workload.JobApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
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

    private final PersistentVolumeClaimApi persistentVolumeClaimApi;
    private final SecretApi secretApi;

    private final ApplicationEventPublisher eventPublisher;
    private final MessageBroadcaster messageBroadcaster;

    public BuildPackListener(BuildPackService buildPackService,
                             ApplicationEventPublisher eventPublisher,
                             MessageBroadcaster messageBroadcaster,
                             PersistentVolumeClaimApi persistentVolumeClaimApi,
                             SecretApi secretApi,
                             PodApi podApi,
                             JobApi jobApi) {
        this.buildPackService = buildPackService;
        this.eventPublisher = eventPublisher;
        this.messageBroadcaster = messageBroadcaster;
        this.persistentVolumeClaimApi = persistentVolumeClaimApi;
        this.secretApi = secretApi;
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

    @EventListener
    @Async
    public void delete(BuildPackDeletedEvent deletedEvent) {
        BuildPack buildPack = deletedEvent.getBuildPack();
        String job = buildPack.getJobResource().getName();
        String namespace = buildPack.getJobResource().getNamespace();
        String persistentVolumeClaim = buildPack.getStorageResource().getPersistentVolumeClaim();
        String secretName = buildPack.getSecretResource().getName();
        try {
            Job read = jobApi.read(namespace, job);
            if (read != null) {
                jobApi.delete(namespace, job);
                log.info("[BuildPackDeletedEvent] delete job '{}'", job);
            }
            PersistentVolumeClaim claim = persistentVolumeClaimApi.read(namespace, persistentVolumeClaim);
            if (claim != null) {
                persistentVolumeClaimApi.delete(persistentVolumeClaim, namespace);
                log.info("[BuildPackDeletedEvent] delete persistentVolumeClaim '{}'", persistentVolumeClaim);
            }
            Secret secret = secretApi.read(namespace, secretName);
            if (secret != null) {
                secretApi.delete(namespace, secretName);
                log.info("[BuildPackDeletedEvent] delete secret '{}'", secretName);
            }

        } catch (Exception ex) {
            log.error("[BuildPackDeletedEvent] error: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void done(BuildPackDoneEvent doneEvent) {
        BuildPack buildPack = doneEvent.getBuildPack();

        buildPack = buildPackService.findOne(buildPack.getId());
        if (buildPack.isDeleted()) {
            log.warn("[{}] user's BuildPack [{}] has been deleted", buildPack.getUser(), buildPack.getId());
            buildPack.setMessage("stopped by delete");
            updateBuildPackDone(buildPack);
            return;
        }
        try {
            boolean success = doneEvent.isSuccess();
            PodList read = podApi.read(buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getLabels());
            Pod pod = read.getItems().get(0);

            String logs = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName());

            buildPack.setMessage(success ? "success" : "failed");
            buildPack.setLogs(logs);

            BuildPack saveOrUpdate = updateBuildPackDone(buildPack);
            log.info("[BuildPackDoneEvent] update [{}] user's BuildPack done [{}]", saveOrUpdate.getUser(), saveOrUpdate.getId());
            //depends on rabbitmq
            messageBroadcaster.broadcast(BuildPackConstant.EXCHANGE_FANOUT_BUILDPACK_MESSAGE, Message.of(saveOrUpdate));
        } catch (Exception ex) {
            log.error("[BuildPackDoneEvent] error: {}", ex.getMessage(), ex);
            buildPack.setMessage(ex.getMessage());
            updateBuildPackDone(buildPack);
        }

    }

    @Async
    @EventListener
    public void started(BuildPackStartedEvent startedEvent) {
        BuildPack buildPack = startedEvent.getBuildPack();
        String namespace = buildPack.getJobResource().getNamespace();
        String jobName = buildPack.getJobResource().getName();
        try {

            while (true) {
                sleep(30);

                buildPack = buildPackService.findOne(buildPack.getId());
                if (buildPack.isDeleted()) {
                    log.warn("[{}] user's BuildPack [{}] has been deleted", buildPack.getUser(), buildPack.getId());
                    buildPack.setMessage("stopped by delete");
                    updateBuildPackDone(buildPack);
                    break;
                }

                Job job = jobApi.read(namespace, jobName);
                BuildPackStatus.JobStatus jobStatus = BuildPackStatus.status(job);

                if (jobStatus == BuildPackStatus.JobStatus.Active) {
                    log.info("[{}] user's BuildPack [{}] is not done yet! job [{}] namespace [{}]", buildPack.getUser(), buildPack.getId(), jobName, namespace);
                }

                if (jobStatus == BuildPackStatus.JobStatus.Ready) {
                    log.info("[{}] user's BuildPack [{}] is ready", buildPack.getUser(), buildPack.getId());
                }

                if (jobStatus == BuildPackStatus.JobStatus.Succeeded || jobStatus == BuildPackStatus.JobStatus.Failed) {
                    eventPublisher.publishEvent(new BuildPackDoneEvent(buildPack, jobStatus == BuildPackStatus.JobStatus.Succeeded));
                    break;
                }
            }

        } catch (Exception e) {
            log.error("[BuildPackStartedEvent] error: {}", e.getMessage(), e);
            buildPack.setMessage(e.getMessage());
            updateBuildPackDone(buildPack);
        }

    }

    @NotNull
    private BuildPack updateBuildPackDone(BuildPack buildPack) {
        buildPack.setDone(true);
        //should be never happened
        Assert.hasText(buildPack.getId(), "BuildPack ID is null");

        return buildPackService.saveOrUpdate(buildPack);
    }

    @Async
    @EventListener
    public void startFailure(BuildPackStartFailureEvent startFailureEvent) {
        try {
            BuildPack buildPack = startFailureEvent.getBuildPack();
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

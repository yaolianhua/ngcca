package io.hotcloud.server.buildpack.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.server.files.FileHelper;
import io.hotcloud.server.message.Message;
import io.hotcloud.server.message.MessageBroadcaster;
import io.hotcloud.vendor.buildpack.*;
import io.hotcloud.vendor.buildpack.event.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static io.hotcloud.common.model.CommonConstant.FAILED_MESSAGE;
import static io.hotcloud.common.model.CommonConstant.SUCCESS_MESSAGE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Deprecated(since = "BuildPackApiV2")
@RequiredArgsConstructor
public class BuildPackListener {

    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
    private final PodClient podApi;
    private final JobClient jobApi;

    private final PersistentVolumeClaimClient persistentVolumeClaimApi;
    private final PersistentVolumeClient persistentVolumeApi;
    private final SecretClient secretApi;

    private final ApplicationEventPublisher eventPublisher;
    private final MessageBroadcaster messageBroadcaster;

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @EventListener
    @Async
    public void artifactUploaded(BuildPackArtifactUploadedEvent event) {
        try {
            BuildPack buildPack = event.getBuildPack();
            String clonedPath = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
            String tarball = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

            Path path = Path.of(clonedPath, tarball);
            boolean deleted = FileHelper.deleteRecursively(path);
            if (deleted) {
                Log.info(BuildPackListener.class.getName(),
                        BuildPackArtifactUploadedEvent.class.getSimpleName(),
                        String.format("[%s] user's tarBall '%s' has been deleted", buildPack.getUser(), tarball));
            }
        } catch (IOException ex) {
            Log.error(BuildPackListener.class.getName(),
                    BuildPackArtifactUploadedEvent.class.getSimpleName(),
                    String.format("%s", ex.getMessage()));
        }

    }

    @EventListener
    @Async
    public void delete(BuildPackDeletedEvent deletedEvent) {
        BuildPack buildPack = deletedEvent.getBuildPack();
        String job = buildPack.getJobResource().getName();
        String namespace = buildPack.getJobResource().getNamespace();
        String persistentVolumeClaim = buildPack.getStorageResource().getPersistentVolumeClaim();
        String persistentVolume = buildPack.getStorageResource().getPersistentVolume();
        String secretName = buildPack.getSecretResource().getName();
        try {
            Job read = jobApi.read(namespace, job);
            if (read != null) {
                jobApi.delete(namespace, job);
                Log.info(BuildPackListener.class.getName(),
                        BuildPackDeletedEvent.class.getSimpleName(),
                        String.format("delete job '%s'", job));
            }
            PersistentVolumeClaim claim = persistentVolumeClaimApi.read(namespace, persistentVolumeClaim);
            if (claim != null) {
                persistentVolumeClaimApi.delete(persistentVolumeClaim, namespace);
                Log.info(BuildPackListener.class.getName(),
                        BuildPackDeletedEvent.class.getSimpleName(),
                        String.format("delete persistentVolumeClaim '%s'", persistentVolumeClaim));
            }
            PersistentVolume volume = persistentVolumeApi.read(persistentVolume);
            if (volume != null && deletedEvent.isPhysically()) {
                persistentVolumeApi.delete(persistentVolume);
                Log.info(BuildPackListener.class.getName(),
                        BuildPackDeletedEvent.class.getSimpleName(),
                        String.format("delete persistentVolume '%s'", persistentVolume));
            }

            Secret secret = secretApi.read(namespace, secretName);
            if (secret != null) {
                secretApi.delete(namespace, secretName);
                Log.info(BuildPackListener.class.getName(),
                        BuildPackDeletedEvent.class.getSimpleName(),
                        String.format("delete secret '%s'", secretName));
            }

        } catch (Exception ex) {
            Log.error(BuildPackListener.class.getName(),
                    BuildPackDeletedEvent.class.getSimpleName(),
                    String.format("%s", ex.getMessage()));
        }
    }

    @EventListener
    @Async
    public void done(BuildPackDoneEvent doneEvent) {
        BuildPack buildPack = doneEvent.getBuildPack();

        buildPack = buildPackService.findOne(buildPack.getId());
        if (buildPack.isDeleted()) {
            Log.warn(BuildPackListener.class.getName(),
                    BuildPackDoneEvent.class.getSimpleName(),
                    String.format("[%s] user's BuildPack [%s] has been deleted", buildPack.getUser(), buildPack.getId()));
            buildPack.setMessage("stopped by delete");
            updateBuildPackDone(buildPack);
            return;
        }
        try {
            boolean success = doneEvent.isSuccess();
            PodList read = podApi.readList(buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getLabels());
            Pod pod = read.getItems().get(0);

            String logs = podApi.logs(buildPack.getJobResource().getNamespace(), pod.getMetadata().getName(), 100);

            buildPack.setMessage(success ? SUCCESS_MESSAGE : FAILED_MESSAGE);
            buildPack.setLogs(logs);

            BuildPack saveOrUpdate = updateBuildPackDone(buildPack);
            Log.info(BuildPackListener.class.getName(),
                    BuildPackDoneEvent.class.getSimpleName(),
                    String.format("update [%s] user's BuildPack done [%s]", saveOrUpdate.getUser(), saveOrUpdate.getId()));
            //depends on rabbitmq
            messageBroadcaster.broadcast(BuildPackConstant.EXCHANGE_FANOUT_BUILDPACK_MESSAGE, Message.of(saveOrUpdate));
        } catch (Exception ex) {
            Log.error(BuildPackListener.class.getName(),
                    BuildPackDoneEvent.class.getSimpleName(),
                    String.format("%s", ex.getMessage()));
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
                    Log.warn(BuildPackListener.class.getName(),
                            BuildPackStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's BuildPack [%s] has been deleted", buildPack.getUser(), buildPack.getId()));
                    buildPack.setMessage("stopped by delete");
                    updateBuildPackDone(buildPack);
                    break;
                }

                ImageBuildStatus jobStatus = buildPackApiV2.getStatus(namespace, jobName);

                if (jobStatus == ImageBuildStatus.Active) {
                    Log.info(BuildPackListener.class.getName(),
                            BuildPackStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's BuildPack [%s] is not done yet! job [%s] namespace [%s]", buildPack.getUser(), buildPack.getId(), jobName, namespace));
                }

                if (jobStatus == ImageBuildStatus.Ready) {
                    Log.info(BuildPackListener.class.getName(),
                            BuildPackStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's BuildPack [%s] is ready", buildPack.getUser(), buildPack.getId()));
                }

                if (jobStatus == ImageBuildStatus.Succeeded || jobStatus == ImageBuildStatus.Failed) {
                    eventPublisher.publishEvent(new BuildPackDoneEvent(buildPack, jobStatus == ImageBuildStatus.Succeeded));
                    break;
                }
            }

        } catch (Exception e) {
            Log.error(BuildPackListener.class.getName(),
                    BuildPackStartedEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
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

            buildPack.setDone(true);
            buildPack.setMessage(throwable.getMessage());

            Assert.hasText(buildPack.getId(), "BuildPack ID is null");
            BuildPack saveOrUpdate = buildPackService.saveOrUpdate(buildPack);
            Log.info(BuildPackListener.class.getName(),
                    BuildPackStartFailureEvent.class.getSimpleName(),
                    String.format("buildPack start failure. update buildPack [%s]", saveOrUpdate.getId()));
        } catch (Throwable e) {
            Log.error(BuildPackListener.class.getName(),
                    BuildPackStartFailureEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

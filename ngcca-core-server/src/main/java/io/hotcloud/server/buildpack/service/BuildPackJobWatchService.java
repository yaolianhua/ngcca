package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.utils.Log;
import io.hotcloud.module.buildpack.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.hotcloud.common.model.CommonConstant.*;
import static io.hotcloud.module.buildpack.ImageBuildStatus.Failed;
import static io.hotcloud.module.buildpack.ImageBuildStatus.Succeeded;

@Component
@RequiredArgsConstructor
public class BuildPackJobWatchService {
    private final BuildPackService buildPackService;
    private final BuildPackApi buildPackApi;
    private final ImageBuildCacheApi imageBuildCacheApi;

    public void mqWatch(BuildPack buildPack) {

        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();
        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Unknown);
        boolean timeout = LocalDateTime.now().compareTo(buildPack.getCreatedAt().plusSeconds(imageBuildCacheApi.getTimeoutSeconds())) > 0;
        try {
            if (timeout) {
                buildPack.setDone(true);
                buildPack.setMessage(TIMEOUT_MESSAGE);
                buildPack.setLogs(buildPackApi.fetchLog(namespace, job));

                buildPackService.saveOrUpdate(buildPack);
                imageBuildCacheApi.setStatus(buildPack.getId(), Failed);

                return;
            }

            ImageBuildStatus status = buildPackApi.getStatus(namespace, job);
            if (Objects.equals(Succeeded, status) || Objects.equals(Failed, status)) {
                buildPack.setDone(true);
                buildPack.setMessage(Objects.equals(Succeeded, status) ? SUCCESS_MESSAGE : FAILED_MESSAGE);
                buildPack.setLogs(buildPackApi.fetchLog(namespace, job));
                buildPackService.saveOrUpdate(buildPack);

                imageBuildCacheApi.setStatus(buildPack.getId(), status);
                return;
            }

            Log.info(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild][%s]. namespace:%s | job:%s | buildPack:%s", status, namespace, job, buildPack.getId()));
            imageBuildCacheApi.setStatus(buildPack.getId(), status);

        } catch (Exception ex) {
            Log.error(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild] exception occur, namespace:%s | job:%s | buildPack:%s | message:%s", namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage("exception occur: " + ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), Failed);
        }
    }

    public void inProcessWatch(BuildPack buildPack) {
        AtomicInteger loopCount = new AtomicInteger();
        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();

        if (!imageBuildCacheApi.tryLock(buildPack.getId())) {
            return;
        }

        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Unknown);
        try {
            while (loopCount.get() < imageBuildCacheApi.getTimeoutSeconds() / 3) {
                TimeUnit.SECONDS.sleep(3);

                buildPack = buildPackService.findOne(buildPack.getId());
                if (Objects.isNull(buildPack) || buildPack.isDeleted()) {
                    Log.warn(BuildPackJobWatchService.class.getName(), "[ImageBuild] BuildPack has been deleted");

                    if (Objects.nonNull(buildPack)) {
                        imageBuildCacheApi.unLock(buildPack.getId());
                    }
                    return;
                }

                ImageBuildStatus status = buildPackApi.getStatus(namespace, job);
                if (Objects.equals(Succeeded, status) || Objects.equals(Failed, status)) {
                    buildPack.setDone(true);
                    buildPack.setMessage(Objects.equals(Succeeded, status) ? SUCCESS_MESSAGE : FAILED_MESSAGE);
                    buildPack.setLogs(buildPackApi.fetchLog(namespace, job));
                    buildPackService.saveOrUpdate(buildPack);

                    imageBuildCacheApi.setStatus(buildPack.getId(), status);
                    imageBuildCacheApi.unLock(buildPack.getId());
                    return;
                }

                Log.info(BuildPackJobWatchService.class.getName(), String.format("[ImageBuild][%s]. namespace:%s | job:%s | buildPack:%s", status, namespace, job, buildPack.getId()));
                imageBuildCacheApi.setStatus(buildPack.getId(), status);

                loopCount.incrementAndGet();
            }

            buildPack.setDone(true);
            buildPack.setMessage(TIMEOUT_MESSAGE);
            buildPack.setLogs(buildPackApi.fetchLog(namespace, job));

            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), Failed);
            imageBuildCacheApi.unLock(buildPack.getId());

        } catch (Exception ex) {
            Log.error(BuildPackJobWatchService.class.getName(), String.format("[ImageBuild] exception occur, namespace:%s | job:%s | buildPack:%s | message:%s", namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage(ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), Failed);
            imageBuildCacheApi.unLock(buildPack.getId());
        }
    }
}

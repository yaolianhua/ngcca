package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.hotcloud.common.api.CommonConstant.*;

@Component
@RequiredArgsConstructor
public class BuildPackWatchService {
    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
    private final KubectlApi kubectlApi;
    private final ImageBuildCacheApi imageBuildCacheApi;

    public void watchCreated(BuildPack buildPack) {
        AtomicInteger loopCount = new AtomicInteger();
        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();

        if (!imageBuildCacheApi.tryLock(buildPack.getId())) {
            return;
        }

        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Unknown);
        try {
            while (loopCount.get() < imageBuildCacheApi.getTimeoutSeconds() / 3){
                TimeUnit.SECONDS.sleep(3);

                buildPack = buildPackService.findOne(buildPack.getId());
                if (Objects.isNull(buildPack) || buildPack.isDeleted()) {
                    Log.warn(BuildPackWatchService.class.getName(), "[ImageBuild] BuildPack has been deleted");

                    if (Objects.nonNull(buildPack)){
                        imageBuildCacheApi.unLock(buildPack.getId());
                    }
                    return;
                }

                ImageBuildStatus status = buildPackApiV2.getStatus(namespace, job);
                switch (status){
                    case Unknown:
                        Log.warn(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko status is [Unknown]. namespace:%s | job:%s | buildPack:%s", namespace, job, buildPack.getId()));
                        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Unknown);
                        break;

                    case Ready:
                        Log.info(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko status is [Ready]. namespace:%s | job:%s | buildPack:%s", namespace, job, buildPack.getId()));
                        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Ready);
                        break;

                    case Active:
                        Log.info(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko status is [Active]. namespace:%s | job:%s | buildPack:%s", namespace, job, buildPack.getId()));
                        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Active);
                        break;

                    case Failed:
                        Log.info(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko status is [Failed]. namespace:%s | job:%s | buildPack:%s", namespace, job, buildPack.getId()));

                        buildPack.setDone(true);
                        buildPack.setMessage(FAILED_MESSAGE);
                        buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

                        buildPackService.saveOrUpdate(buildPack);
                        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
                        imageBuildCacheApi.unLock(buildPack.getId());
                        return;

                    case Succeeded:
                        Log.info(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko status is [Succeeded]. namespace:%s | job:%s | buildPack:%s", namespace, job, buildPack.getId()));

                        buildPack.setDone(true);
                        buildPack.setMessage(SUCCESS_MESSAGE);
                        buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

                        buildPackService.saveOrUpdate(buildPack);
                        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Succeeded);
                        imageBuildCacheApi.unLock(buildPack.getId());
                        return;

                    default:

                        break;
                }

                loopCount.incrementAndGet();
            }

            buildPack.setDone(true);
            buildPack.setMessage(TIMEOUT_MESSAGE);
            buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
            imageBuildCacheApi.unLock(buildPack.getId());

            Boolean delete = kubectlApi.delete(namespace, buildPack.getYaml());
            Log.warn(BuildPackWatchService.class.getName(), String.format("[ImageBuild] Kaniko job has been timeout, Deleted kaniko job [%s]. namespace:%s | job:%s | buildPack:%s", delete, namespace, job, buildPack.getId()));
        }catch (Exception ex){
            Boolean delete = kubectlApi.delete(namespace, buildPack.getYaml());
            Log.error(BuildPackWatchService.class.getName(), String.format("[ImageBuild] exception occur, Deleted kaniko job [%s]. namespace:%s | job:%s | buildPack:%s | message:%s", delete, namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage(ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
            imageBuildCacheApi.unLock(buildPack.getId());
        }
    }

    public void watchDeleted(BuildPack buildPack) {
        try {
            Boolean delete = kubectlApi.delete(buildPack.getJobResource().getNamespace(), buildPack.getYaml());
            Log.info(BuildPackWatchService.class.getName(), String.format("Deleted BuildPack k8s resources [%s]. namespace [%s] job [%s]", delete, buildPack.getJobResource().getNamespace(), buildPack.getJobResource().getName()));
        } catch (Exception ex) {
            Log.error(BuildPackWatchService.class.getName(), String.format("Deleted BuildPack k8s resources exception: [%s]", ex.getMessage()));
        }
    }
}

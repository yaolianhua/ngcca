package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.common.model.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.hotcloud.buildpack.api.core.ImageBuildStatus.Failed;
import static io.hotcloud.buildpack.api.core.ImageBuildStatus.Succeeded;
import static io.hotcloud.common.model.CommonConstant.*;

@Component
@RequiredArgsConstructor
public class BuildPackInProcessWatchService {
    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
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
                    Log.warn(BuildPackInProcessWatchService.class.getName(), "[ImageBuild] BuildPack has been deleted");

                    if (Objects.nonNull(buildPack)) {
                        imageBuildCacheApi.unLock(buildPack.getId());
                    }
                    return;
                }

                ImageBuildStatus status = buildPackApiV2.getStatus(namespace, job);
                if (Objects.equals(Succeeded, status) || Objects.equals(Failed, status)) {
                    buildPack.setDone(true);
                    buildPack.setMessage(Objects.equals(Succeeded, status) ? SUCCESS_MESSAGE : FAILED_MESSAGE);
                    buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));
                    buildPackService.saveOrUpdate(buildPack);

                    imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
                    imageBuildCacheApi.unLock(buildPack.getId());
                    return;
                }

                Log.info(BuildPackInProcessWatchService.class.getName(), String.format("[ImageBuild][%s]. namespace:%s | job:%s | buildPack:%s", status, namespace, job, buildPack.getId()));
                imageBuildCacheApi.setStatus(buildPack.getId(), status);

                loopCount.incrementAndGet();
            }

            buildPack.setDone(true);
            buildPack.setMessage(TIMEOUT_MESSAGE);
            buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
            imageBuildCacheApi.unLock(buildPack.getId());

        }catch (Exception ex){
            Log.error(BuildPackInProcessWatchService.class.getName(), String.format("[ImageBuild] exception occur, namespace:%s | job:%s | buildPack:%s | message:%s", namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage(ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
            imageBuildCacheApi.unLock(buildPack.getId());
        }
    }
}

package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.log.Log;
import io.hotcloud.module.buildpack.BuildPackApi;
import io.hotcloud.module.buildpack.BuildPackCacheApi;
import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.buildpack.model.JobState;
import io.hotcloud.server.buildpack.BuildPackProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.*;
import static io.hotcloud.module.buildpack.model.JobState.FAILED;
import static io.hotcloud.module.buildpack.model.JobState.SUCCEEDED;

@Component
@RequiredArgsConstructor
public class BuildPackJobWatchService {
    private final BuildPackService buildPackService;
    private final BuildPackApi buildPackApi;
    private final BuildPackCacheApi buildPackCacheApi;
    private final BuildPackProperties buildPackProperties;

    public void mqWatch(BuildPack buildPack) {

        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();
        buildPackCacheApi.cacheBuildPackState(buildPack.getId(), JobState.UNKNOWN);
        boolean timeout = LocalDateTime.now().compareTo(buildPack.getCreatedAt().plusSeconds(buildPackProperties.getBuildTimeoutSecond())) > 0;
        try {
            if (timeout) {
                buildPack.setDone(true);
                buildPack.setMessage(TIMEOUT_MESSAGE);
                buildPack.setLogs(buildPackApi.fetchLog(namespace, job));

                buildPackService.saveOrUpdate(buildPack);
                buildPackCacheApi.cacheBuildPackState(buildPack.getId(), FAILED);

                return;
            }

            JobState status = buildPackApi.getStatus(namespace, job);
            if (Objects.equals(SUCCEEDED, status) || Objects.equals(FAILED, status)) {
                buildPack.setDone(true);
                buildPack.setMessage(Objects.equals(SUCCEEDED, status) ? SUCCESS_MESSAGE : FAILED_MESSAGE);
                buildPack.setLogs(buildPackApi.fetchLog(namespace, job));
                buildPackService.saveOrUpdate(buildPack);

                buildPackCacheApi.cacheBuildPackState(buildPack.getId(), status);
                return;
            }

            Log.info(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild][%s]. namespace:%s | job:%s | buildPack:%s", status, namespace, job, buildPack.getId()));
            buildPackCacheApi.cacheBuildPackState(buildPack.getId(), status);

        } catch (Exception ex) {
            Log.error(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild] exception occur, namespace:%s | job:%s | buildPack:%s | message:%s", namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage("exception occur: " + ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            buildPackCacheApi.cacheBuildPackState(buildPack.getId(), FAILED);
        }
    }
}

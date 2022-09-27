package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackApiV2;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.event.BuildPackDeletedEventV2;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEventV2;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.hotcloud.common.api.CommonConstant.*;

@Component
@RequiredArgsConstructor
public class BuildPackK8sService {
    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
    private final KubectlApi kubectlApi;
    private final Cache cache;

    public void processBuildPackCreatedBlocked(BuildPack buildPack) {
        AtomicInteger loopCount = new AtomicInteger();
        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();

        Object o = cache.get(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));
        if (Objects.nonNull(o)){
            return;
        }
        cache.put(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()), Boolean.TRUE);
        Integer timeout = cache.get(CK_IMAGEBUILD_TIMEOUT_SECONDS, Integer.class);
        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Unknown.name());
        try{
            while (loopCount.get() < timeout / 3){
                TimeUnit.SECONDS.sleep(3);

                buildPack = buildPackService.findOne(buildPack.getId());
                if (Objects.isNull(buildPack) || buildPack.isDeleted()) {
                    Log.warn(BuildPackListenerV2.class.getName(),
                            BuildPackStartedEventV2.class.getSimpleName(),
                            "[ImageBuild] BuildPack has been deleted");

                    if (Objects.nonNull(buildPack)){
                        cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));
                    }
                    return;
                }

                BuildPackApiV2.KanikoStatus status = buildPackApiV2.getStatus(namespace, job);
                switch (status){
                    case Unknown:
                        Log.warn(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Unknown]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));
                        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Unknown.name());
                        break;

                    case Ready:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Ready]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        System.out.println("***************************** Print Kaniko Job log ******************************");
                        System.out.println(buildPackApiV2.fetchLog(namespace, job));
                        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Ready.name());
                        break;

                    case Active:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Active]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        System.out.println("***************************** Print Kaniko Job log ******************************");
                        System.out.println(buildPackApiV2.fetchLog(namespace, job));
                        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Active.name());
                        break;

                    case Failed:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Failed]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        buildPack.setDone(true);
                        buildPack.setMessage(FAILED_MESSAGE);
                        buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

                        buildPackService.saveOrUpdate(buildPack);
                        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Failed.name());
                        cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));
                        return;

                    case Succeeded:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Succeeded]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        buildPack.setDone(true);
                        buildPack.setMessage(SUCCESS_MESSAGE);
                        buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

                        buildPackService.saveOrUpdate(buildPack);
                        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Succeeded.name());
                        cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));
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
            cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Failed.name());
            cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));

            Boolean delete = kubectlApi.delete(namespace, buildPack.getYaml());
            Log.warn(BuildPackListenerV2.class.getName(),
                    BuildPackStartedEventV2.class.getSimpleName(),
                    String.format("[ImageBuild] Kaniko job has been timeout, Deleted kaniko job [%s]. namespace:%s | job:%s | buildPack:%s",
                            delete, namespace, job, buildPack.getId()));
        }catch (Exception ex){
            Boolean delete = kubectlApi.delete(namespace, buildPack.getYaml());
            Log.error(BuildPackListenerV2.class.getName(),
                    BuildPackStartedEventV2.class.getSimpleName(),
                    String.format("[ImageBuild] exception occur, Deleted kaniko job [%s]. namespace:%s | job:%s | buildPack:%s | message:%s",
                            delete, namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage(ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPack.getId()), BuildPackApiV2.KanikoStatus.Failed.name());
            cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPack.getId()));
        }
    }

    public void processBuildPackDeleted(BuildPack buildPack) {
        try {
            Boolean delete = kubectlApi.delete(buildPack.getJobResource().getNamespace(), buildPack.getYaml());
            Log.info(BuildPackListenerV2.class.getName(),
                    BuildPackDeletedEventV2.class.getSimpleName(),
                    String.format("Deleted BuildPack k8s resources [%s]", delete));
        } catch (Exception ex) {
            Log.error(BuildPackListenerV2.class.getName(),
                    BuildPackDeletedEventV2.class.getSimpleName(),
                    String.format("Deleted BuildPack k8s resources exception: [%s]", ex.getMessage()));
        }
    }
}

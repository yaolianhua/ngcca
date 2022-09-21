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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.hotcloud.common.api.CommonConstant.*;

@Component
@RequiredArgsConstructor
public class BuildPackListenerV2 {

    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
    private final KubectlApi kubectlApi;
    private final Cache cache;

    @Async
    @EventListener
    public void started(BuildPackStartedEventV2 startedEvent) {
        AtomicInteger loopCount = new AtomicInteger();
        BuildPack buildPack = startedEvent.getBuildPack();
        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();

        Integer timeout = cache.get(IMAGEBUILD_TIMEOUT_SECONDS, Integer.class);

        try{
            while (loopCount.get() < timeout / 20){
                TimeUnit.SECONDS.sleep(20);

                buildPack = buildPackService.findOne(buildPack.getId());
                if (Objects.isNull(buildPack) || buildPack.isDeleted()) {
                    Log.warn(BuildPackListenerV2.class.getName(),
                            BuildPackStartedEventV2.class.getSimpleName(),
                            "[ImageBuild] BuildPack has been deleted");
                    return;
                }

                BuildPackApiV2.KanikoStatus status = buildPackApiV2.getStatus(namespace, job);
                switch (status){
                    case Unknown:
                        Log.warn(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Unknown]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));
                        break;

                    case Ready:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Ready]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        System.out.println("***************************** Print Kaniko Job log ******************************");
                        System.out.println(buildPackApiV2.fetchLog(namespace, job));
                        break;

                    case Active:
                        Log.info(BuildPackListenerV2.class.getName(),
                                BuildPackStartedEventV2.class.getSimpleName(),
                                String.format("[ImageBuild] Kaniko status is [Active]. namespace:%s | job:%s | buildPack:%s",
                                        namespace, job, buildPack.getId()));

                        System.out.println("***************************** Print Kaniko Job log ******************************");
                        System.out.println(buildPackApiV2.fetchLog(namespace, job));
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
        }
    }

    @Async
    @EventListener
    public void deleted(BuildPackDeletedEventV2 deletedEventV2){
        BuildPack buildPack = deletedEventV2.getBuildPack();
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

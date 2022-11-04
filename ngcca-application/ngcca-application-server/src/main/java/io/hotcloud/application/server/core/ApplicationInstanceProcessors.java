package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import io.hotcloud.buildpack.api.core.ImageBuildCacheApi;
import io.hotcloud.buildpack.api.core.ImageBuildStatus;
import io.hotcloud.common.api.Log;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ApplicationInstanceProcessors {

    private final List<ApplicationInstanceProcessor<ApplicationInstance>> processors;
    private final ImageBuildCacheApi imageBuildCacheApi;

    public ApplicationInstanceProcessors(List<ApplicationInstanceProcessor<ApplicationInstance>> processors, ImageBuildCacheApi imageBuildCacheApi) {
        this.processors = processors;
        this.imageBuildCacheApi = imageBuildCacheApi;
    }

    @SneakyThrows
    public void processCreate (ApplicationInstance instance){

        processors.sort(Comparator.comparingInt(ApplicationInstanceProcessor::order));

        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            //wait build image done!
            if (Objects.equals(processor.getType(), ApplicationInstanceProcessor.Type.ImageBuild)){
                processor.processCreate(instance);
                if (StringUtils.hasText(instance.getBuildPackId())) {
                    while (true){
                        TimeUnit.SECONDS.sleep(1);
                        ImageBuildStatus status = imageBuildCacheApi.getStatus(instance.getBuildPackId());
                        if (Objects.equals(ImageBuildStatus.Succeeded, status)){
                            Log.info(ApplicationInstanceProcessors.class.getName(), String.format("[%s] user's application instance [%s] image build pipeline succeed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            break;
                        }
                        if (Objects.equals(ImageBuildStatus.Failed, status)){
                            Log.error(ApplicationInstanceProcessors.class.getName(), String.format("[%s] user's application instance [%s] pipeline stop. image build failed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            return;
                        }
                    }
                }
                continue;
            }
            processor.processCreate(instance);

        }

    }

    public void processDelete (ApplicationInstance instance){
        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            processor.processDelete(instance);
        }
    }
}

package io.hotcloud.service.application.processor;

import io.hotcloud.common.log.Log;
import io.hotcloud.module.application.ApplicationInstance;
import io.hotcloud.module.application.ApplicationInstanceProcessor;
import io.hotcloud.module.buildpack.BuildPackCacheApi;
import io.hotcloud.module.buildpack.model.JobState;
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
    private final BuildPackCacheApi buildPackCacheApi;

    public ApplicationInstanceProcessors(List<ApplicationInstanceProcessor<ApplicationInstance>> processors, BuildPackCacheApi buildPackCacheApi) {
        this.processors = processors;
        this.buildPackCacheApi = buildPackCacheApi;
    }

    @SneakyThrows
    public void processCreate(ApplicationInstance instance) {

        processors.sort(Comparator.comparingInt(ApplicationInstanceProcessor::order));

        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            //wait build image done!
            if (Objects.equals(processor.getType(), ApplicationInstanceProcessor.Type.ImageBuild)) {
                processor.processCreate(instance);
                if (StringUtils.hasText(instance.getBuildPackId())) {
                    while (true) {
                        TimeUnit.SECONDS.sleep(1);
                        JobState status = buildPackCacheApi.getBuildPackState(instance.getBuildPackId());
                        if (Objects.equals(JobState.SUCCEEDED, status)) {
                            Log.info(this, null, String.format("[%s] user's application instance [%s] image build pipeline succeed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            break;
                        }
                        if (Objects.equals(JobState.FAILED, status)) {
                            Log.error(this, null, String.format("[%s] user's application instance [%s] pipeline stop. image build failed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            return;
                        }
                    }
                }
                continue;
            }
            processor.processCreate(instance);

        }

    }

    public void processDelete(ApplicationInstance instance) {
        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            processor.processDelete(instance);
        }
    }
}

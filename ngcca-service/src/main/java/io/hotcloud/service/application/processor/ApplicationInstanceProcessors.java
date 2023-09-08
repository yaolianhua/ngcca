package io.hotcloud.service.application.processor;

import io.hotcloud.common.log.Log;
import io.hotcloud.service.application.ApplicationInstanceProcessor;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.buildpack.BuildPackCacheApi;
import io.hotcloud.service.buildpack.model.JobState;
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

    public void processCreate(ApplicationInstance instance) {

        processors.sort(Comparator.comparingInt(ApplicationInstanceProcessor::order));

        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            //wait build image done!
            if (Objects.equals(processor.getType(), ApplicationInstanceProcessor.Type.IMAGE_BUILD)) {
                processor.processCreate(instance);
                if (StringUtils.hasText(instance.getBuildPackId())) {
                    while (true) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            //
                            Thread.currentThread().interrupt();
                        }
                        JobState status = buildPackCacheApi.getBuildPackState(instance.getBuildPackId());
                        if (Objects.equals(JobState.SUCCEEDED, status)) {
                            Log.info(this, null, String.format("[%s] user's application instance [%s] image build succeed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            break;
                        }
                        if (Objects.equals(JobState.FAILED, status)) {
                            Log.error(this, null, String.format("[%s] user's application instance [%s] deploy failed. image build failed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            processor.processFailed(instance);
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

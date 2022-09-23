package io.hotcloud.application.api.core;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.cache.Cache;
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
    private final Cache cache;

    public ApplicationInstanceProcessors(List<ApplicationInstanceProcessor<ApplicationInstance>> processors, Cache cache) {
        this.processors = processors;
        this.cache = cache;
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
                        String status = cache.get(String.format(CommonConstant.CK_IMAGEBUILD_STATUS, instance.getBuildPackId()), String.class);
                        if (Objects.equals("Succeeded", status)){
                            Log.error(ApplicationInstanceProcessors.class.getName(),
                                    String.format("[%s] user's application instance [%s] image build pipeline succeed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            break;
                        }
                        if (Objects.equals("Failed", status)){
                            Log.error(ApplicationInstanceProcessors.class.getName(),
                                    String.format("[%s] user's application instance [%s] pipeline stop. image build failed [%s]", instance.getUser(), instance.getName(), instance.getBuildPackId()));
                            return;
                        }
                    }
                }
            }
            else {
                processor.processCreate(instance);
            }

        }

    }

    public void processDelete (ApplicationInstance instance){
        for (ApplicationInstanceProcessor<ApplicationInstance> processor : processors) {
            processor.processDelete(instance);
        }
    }
}

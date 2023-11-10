package io.hotcloud.service.application.processor;

import io.hotcloud.common.log.Log;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.buildpack.BuildPackCacheApi;
import io.hotcloud.service.buildpack.model.JobState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApplicationInstanceProcessors {
    private final ApplicationInstanceDeploymentProcessor deploymentProcessor;
    private final ApplicationInstanceImageBuildProcessor imageBuildProcessor;
    private final ApplicationInstanceIngressProcessor ingressProcessor;
    private final ApplicationInstanceServiceProcessor serviceProcessor;
    private final BuildPackCacheApi buildPackCacheApi;

    public void processCreate(ApplicationInstance instance) {

        //start build
        imageBuildProcessor.processCreate(instance);

        //wait build done
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
                    imageBuildProcessor.processFailed(instance);
                    return;
                }
            }
        }

        //create service
        serviceProcessor.processCreate(instance);

        //create ingress if need
        ingressProcessor.processCreate(instance);

        //create deployment
        deploymentProcessor.processCreate(instance);

    }

    public void processDelete(ApplicationInstance instance) {
        //
        imageBuildProcessor.processDelete(instance);

        ingressProcessor.processDelete(instance);

        serviceProcessor.processDelete(instance);

        deploymentProcessor.processDelete(instance);
    }
}

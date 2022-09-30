package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.core.ApplicationDeploymentCacheApi;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApplicationInstanceK8sService {

    private final DeploymentApi deploymentApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationDeploymentCacheApi applicationDeploymentCacheApi;

    public void processApplicationCreatedBlocked (ApplicationInstance instance){

        if (!applicationDeploymentCacheApi.tryLock(instance.getId())) {
            return;
        }
        try {
            while (true) {

                TimeUnit.SECONDS.sleep(1);
                ApplicationInstance applicationInstance = applicationInstanceService.findOne(instance.getId());
                //if deleted
                if (applicationInstance.isDeleted()) {
                    Log.warn(ApplicationInstanceK8sService.class.getName(), String.format("[%s] user's application instance [%s] has been deleted", instance.getUser(), instance.getName()));
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }
                if (applicationInstance.isSuccess()){
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }

                //if timeout
                int timeout = LocalDateTime.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(applicationDeploymentCacheApi.getTimeoutSeconds()));
                if (timeout > 0) {
                    applicationInstance.setMessage(CommonConstant.TIMEOUT_MESSAGE);
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }

                //流程还未走到deployment
                Deployment deployment = deploymentApi.read(applicationInstance.getNamespace(), applicationInstance.getName());
                if (Objects.nonNull(deployment)) {
                    boolean ready = ApplicationInstanceDeploymentStatus.isReady(deployment, instance.getReplicas());
                    if (!ready) {
                        Log.info(ApplicationInstanceK8sService.class.getName(), String.format("[%s] user's application instance deployment [%s] is not ready!", applicationInstance.getUser(), applicationInstance.getName()));
                    }

                    //deployment success
                    if (ready) {
                        Log.info(ApplicationInstanceK8sService.class.getName(),  String.format("[%s] user's application instance deployment [%s] success!", applicationInstance.getUser(), applicationInstance.getName()));
                        applicationInstance.setMessage(CommonConstant.SUCCESS_MESSAGE);
                        applicationInstance.setSuccess(true);
                        applicationInstanceService.saveOrUpdate(applicationInstance);
                        applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                        return;
                    }
                }
            }

        } catch (Exception e) {
            Log.error(ApplicationInstanceK8sService.class.getName(), String.format("%s", e.getMessage()));

            instance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(instance);
            applicationDeploymentCacheApi.unLock(instance.getId());
        }

    }
}

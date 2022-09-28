package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.application.api.core.event.ApplicationInstanceCreateEvent;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApplicationInstanceListener {

    private final DeploymentApi deploymentApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final BuildPackService buildPackService;
    private final ApplicationInstanceProcessors processors;
    private final Cache cache;

    @SneakyThrows
    @EventListener
    @Async
    public void applicationInstanceCreate (ApplicationInstanceCreateEvent createEvent){
        ApplicationInstance instance = createEvent.getInstance();

        try {
            processors.processCreate(instance);

            while (true) {

                TimeUnit.SECONDS.sleep(1);
                ApplicationInstance applicationInstance = applicationInstanceService.findOne(instance.getId());
                //if deleted
                if (applicationInstance.isDeleted()) {
                    Log.warn(ApplicationInstanceListener.class.getName(),
                            ApplicationInstanceCreateEvent.class.getSimpleName(),
                            String.format("[%s] user's application instance [%s] has been deleted", instance.getUser(), instance.getName()));
                    break;
                }
                if (applicationInstance.isSuccess()){
                    break;
                }

                //if timeout
                Integer timeoutSeconds = cache.get(CommonConstant.CK_DEPLOYMENT_TIMEOUT_SECONDS, Integer.class);
                int timeout = LocalDateTime.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(timeoutSeconds));
                if (timeout > 0) {
                    applicationInstance.setMessage(CommonConstant.TIMEOUT_MESSAGE);
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    break;
                }

                //deploying
                Deployment deployment = deploymentApi.read(applicationInstance.getNamespace(), applicationInstance.getName());
                if (Objects.isNull(deployment)){
                    applicationInstance.setMessage(CommonConstant.FAILED_MESSAGE);
                    applicationInstance.setSuccess(false);
                    if (StringUtils.hasText(applicationInstance.getBuildPackId())){
                        BuildPack fetched = buildPackService.findOne(applicationInstance.getBuildPackId());
                        applicationInstance.setMessage(fetched.getLogs());
                    }
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    break;
                }
                boolean ready = ApplicationInstanceDeploymentStatus.isReady(deployment, instance.getReplicas());
                if (!ready) {
                    Log.info(ApplicationInstanceListener.class.getName(),
                            ApplicationInstanceCreateEvent.class.getSimpleName(),
                            String.format("[%s] user's application instance deployment [%s] is not ready!",
                                    applicationInstance.getUser(), applicationInstance.getName()));
                }

                //deployment success
                if (ready) {
                    applicationInstance.setMessage(CommonConstant.SUCCESS_MESSAGE);
                    applicationInstance.setSuccess(true);
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    break;
                }

            }

        } catch (Exception e) {
            Log.error(ApplicationInstanceListener.class.getName(),
                    ApplicationInstanceCreateEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));

            instance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(instance);
            throw e;
        }

    }
}

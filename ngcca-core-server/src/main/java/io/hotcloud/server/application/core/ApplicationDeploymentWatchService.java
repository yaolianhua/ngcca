package io.hotcloud.server.application.core;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.module.application.core.ApplicationDeploymentCacheApi;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationDeploymentWatchService {

    private final DeploymentClient deploymentApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationDeploymentCacheApi applicationDeploymentCacheApi;
    private final PodClient podApi;
    private final KubectlClient kubectlApi;


    public void mqWatch(ApplicationInstance applicationInstance) {

        try {
            //if timeout
            int timeout = LocalDateTime.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(applicationDeploymentCacheApi.getTimeoutSeconds()));
            if (timeout > 0) {
                String timeoutMessage = retrieveK8sEventsMessage(applicationInstance);
                applicationInstance.setMessage(timeoutMessage);
                applicationInstanceService.saveOrUpdate(applicationInstance);
                return;
            }

            Deployment deployment = deploymentApi.read(applicationInstance.getNamespace(), applicationInstance.getName());
            if (Objects.nonNull(deployment)) {
                boolean ready = ApplicationInstanceDeploymentStatus.isReady(deployment, applicationInstance.getReplicas());
                if (!ready) {
                    Log.info(ApplicationRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's application instance deployment [%s] is not ready!", applicationInstance.getUser(), applicationInstance.getName()));
                    return;
                }

                //deployment success
                Log.info(ApplicationRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's application instance deployment [%s] deploy success!", applicationInstance.getUser(), applicationInstance.getName()));
                applicationInstance.setMessage(CommonConstant.SUCCESS_MESSAGE);
                applicationInstance.setSuccess(true);
                applicationInstanceService.saveOrUpdate(applicationInstance);
            }


        } catch (Exception e) {
            Log.error(ApplicationRabbitMQK8sEventsListener.class.getName(), String.format("%s", e.getMessage()));

            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
        }

    }

    @NotNull
    private String retrieveK8sEventsMessage(ApplicationInstance applicationInstance) {
        String timeoutMessage = CommonConstant.TIMEOUT_MESSAGE;
        PodList podList = podApi.readList(applicationInstance.getNamespace(),
                Map.of(CommonConstant.K8S_APP_BUSINESS_DATA_ID, applicationInstance.getId(), CommonConstant.K8S_APP, applicationInstance.getName())
        );
        if (Objects.nonNull(podList) && !CollectionUtils.isEmpty(podList.getItems())) {
            List<String> podNameList = podList.getItems()
                    .stream()
                    .map(e -> e.getMetadata().getName())
                    .collect(Collectors.toList());

            timeoutMessage = podNameList.stream()
                    .map(pod -> kubectlApi.namespacedPodEvents(applicationInstance.getNamespace(), pod))
                    .flatMap(Collection::stream)
                    .filter(event -> Objects.equals("Warning", event.getType()))
                    .map(Event::getMessage)
                    .distinct()
                    .collect(Collectors.joining("\n"));
        }
        return timeoutMessage;
    }

    public void inProcessWatch(ApplicationInstance instance) {

        if (!applicationDeploymentCacheApi.tryLock(instance.getId())) {
            return;
        }
        try {
            while (true) {

                TimeUnit.SECONDS.sleep(1);
                ApplicationInstance applicationInstance = applicationInstanceService.findOne(instance.getId());
                //if deleted
                if (applicationInstance.isDeleted()) {
                    Log.warn(ApplicationDeploymentWatchService.class.getName(), String.format("[%s] user's application instance [%s] has been deleted", instance.getUser(), instance.getName()));
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }
                if (applicationInstance.isSuccess()) {
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }

                //if timeout
                int timeout = LocalDateTime.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(applicationDeploymentCacheApi.getTimeoutSeconds()));
                if (timeout > 0) {
                    String timeoutMessage = retrieveK8sEventsMessage(applicationInstance);
                    applicationInstance.setMessage(timeoutMessage);
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                    return;
                }

                //流程还未走到deployment
                Deployment deployment = deploymentApi.read(applicationInstance.getNamespace(), applicationInstance.getName());
                if (Objects.nonNull(deployment)) {
                    boolean ready = ApplicationInstanceDeploymentStatus.isReady(deployment, instance.getReplicas());
                    if (!ready) {
                        Log.debug(ApplicationDeploymentWatchService.class.getName(), String.format("[%s] user's application instance deployment [%s] is not ready!", applicationInstance.getUser(), applicationInstance.getName()));
                    }

                    //deployment success
                    if (ready) {
                        Log.info(ApplicationDeploymentWatchService.class.getName(), String.format("[%s] user's application instance deployment [%s] success!", applicationInstance.getUser(), applicationInstance.getName()));
                        applicationInstance.setMessage(CommonConstant.SUCCESS_MESSAGE);
                        applicationInstance.setSuccess(true);
                        applicationInstanceService.saveOrUpdate(applicationInstance);
                        applicationDeploymentCacheApi.unLock(applicationInstance.getId());
                        return;
                    }
                }
            }

        } catch (Exception e) {
            Log.error(ApplicationDeploymentWatchService.class.getName(), String.format("%s", e.getMessage()));

            instance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(instance);
            applicationDeploymentCacheApi.unLock(instance.getId());
        }

    }
}

package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.core.ApplicationDeploymentCacheApi;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.DeploymentApi;
import io.hotcloud.kubernetes.api.KubectlApi;
import io.hotcloud.kubernetes.api.PodApi;
import lombok.RequiredArgsConstructor;
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
public class ApplicationInstanceK8sService {

    private final DeploymentApi deploymentApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationDeploymentCacheApi applicationDeploymentCacheApi;
    private final PodApi podApi;
    private final KubectlApi kubectlApi;

    public void processApplicationCreatedBlocked(ApplicationInstance instance) {

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
                    String timeoutMessage = CommonConstant.TIMEOUT_MESSAGE;
                    PodList podList = podApi.read(applicationInstance.getNamespace(),
                            Map.of(CommonConstant.K8S_APP_BUSINESS_DATA_ID, instance.getId(), CommonConstant.K8S_APP, applicationInstance.getName())
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
                        Log.debug(ApplicationInstanceK8sService.class.getName(), String.format("[%s] user's application instance deployment [%s] is not ready!", applicationInstance.getUser(), applicationInstance.getName()));
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

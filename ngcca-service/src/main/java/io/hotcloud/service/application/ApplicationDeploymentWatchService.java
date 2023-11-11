package io.hotcloud.service.application;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.service.application.model.ApplicationInstance;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationDeploymentWatchService {

    private final DeploymentClient deploymentApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationProperties applicationProperties;
    private final PodClient podApi;
    private final KubectlClient kubectlApi;


    public void watch(ApplicationInstance applicationInstance) {

        try {
            //if timeout
            int timeout = Instant.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(applicationProperties.getDeploymentTimeoutSecond()));
            if (timeout > 0) {
                String timeoutMessage = retrieveK8sEventsMessage(applicationInstance);
                applicationInstance.setMessage(CommonConstant.TIMEOUT_MESSAGE + ":" + timeoutMessage);
                applicationInstance.setProgress(100);
                applicationInstanceService.saveOrUpdate(applicationInstance);
                Log.warn(this, null, String.format("[%s] user's application instance deployment [%s] deploy timeout", applicationInstance.getUser(), applicationInstance.getName()));
                return;
            }

            Deployment deployment = deploymentApi.read(applicationInstance.getNamespace(), applicationInstance.getName());
            if (Objects.nonNull(deployment)) {
                boolean ready = ApplicationInstanceDeploymentStatus.isReady(deployment, applicationInstance.getReplicas());
                if (!ready) {
                    Log.info(this, null, String.format("[%s] user's application instance deployment [%s] is not ready!", applicationInstance.getUser(), applicationInstance.getName()));
                    applicationInstance.setMessage(CommonConstant.APPLICATION_DEPLOYING_MESSAGE);
                    applicationInstance.setProgress(80);
                    applicationInstanceService.saveOrUpdate(applicationInstance);
                    return;
                }

                //deployment success
                Log.info(this, null, String.format("[%s] user's application instance deployment [%s] deploy success!", applicationInstance.getUser(), applicationInstance.getName()));
                applicationInstance.setMessage(CommonConstant.SUCCESS_MESSAGE);
                applicationInstance.setSuccess(true);
                applicationInstance.setProgress(100);
                applicationInstanceService.saveOrUpdate(applicationInstance);
            }


        } catch (Exception e) {
            Log.error(this, null, e.getMessage());

            applicationInstance.setMessage(e.getMessage());
            applicationInstance.setProgress(100);
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

}

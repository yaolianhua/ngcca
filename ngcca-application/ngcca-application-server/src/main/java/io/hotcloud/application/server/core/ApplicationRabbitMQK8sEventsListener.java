package io.hotcloud.application.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.core.ApplicationDeploymentCacheApi;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstancePlayer;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(
        name = ApplicationProperties.PROPERTIES_TYPE_NAME,
        havingValue = ApplicationProperties.RABBITMQ
)
@RequiredArgsConstructor
@Slf4j
public class ApplicationRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationInstancePlayer applicationInstancePlayer;
    private final ApplicationDeploymentCacheApi deploymentCacheApi;
    private final PodClient podApi;
    private final KubectlClient kubectlApi;
    private final DeploymentClient deploymentApi;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_APPLICATION),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT)
                    )
            }
    )
    public void subscribe(String message) {

        try {
            WatchMessageBody messageBody = convertWatchMessageBody(message).getData();
            if (!Objects.equals(WorkloadsType.Deployment.name(), messageBody.getKind())){
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)){
                return;
            }
            ApplicationInstance fetched = applicationInstanceService.findOne(businessId);
            if (Objects.isNull(fetched)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())){
                log.info("Application Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                applicationInstancePlayer.delete(businessId);
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())) {
                if (fetched.isSuccess()) {
                    return;
                }
                if (fetched.isDeleted()) {
                    Log.warn(ApplicationRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's application instance [%s] has been deleted", fetched.getUser(), fetched.getName()));
                    return;
                }
                log.info("Application [{}] {} events: {}/{}/{}", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                this.watch(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())){
                log.info("Application error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        }catch (Exception e){
            Log.error(ApplicationRabbitMQK8sEventsListener.class.getName(), e.getMessage());
        }

    }

    private Message<WatchMessageBody> convertWatchMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCACommonException(e.getMessage());
        }
    }

    private void watch(ApplicationInstance applicationInstance) {

        try {
            //if timeout
            int timeout = LocalDateTime.now().compareTo(applicationInstance.getCreatedAt().plusSeconds(deploymentCacheApi.getTimeoutSeconds()));
            if (timeout > 0) {
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

}

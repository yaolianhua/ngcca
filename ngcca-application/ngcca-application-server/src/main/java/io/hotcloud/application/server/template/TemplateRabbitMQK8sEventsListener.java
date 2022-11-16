package io.hotcloud.application.server.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.template.TemplateDeploymentCacheApi;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.YamlBody;
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
import org.springframework.util.Assert;
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
public class TemplateRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final TemplateInstanceService templateInstanceService;
    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateDeploymentCacheApi templateDeploymentCacheApi;
    private final DeploymentClient deploymentApi;
    private final ServiceClient serviceApi;
    private final KubectlClient kubectlApi;
    private final PodClient podApi;


    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_TEMPLATE),
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
            TemplateInstance template = templateInstanceService.findByUuid(businessId);
            if (Objects.isNull(template)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())){
                log.info("Template Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                templateInstancePlayer.delete(template.getId());
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())){
                if (template.isSuccess()) {
                    return;
                }
                log.info("Template [{}] {} events: {}/{}/{}", template.getId(), messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                this.watch(template);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())){
                log.info("Application error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        }catch (Exception e){
            Log.error(TemplateRabbitMQK8sEventsListener.class.getName(), e.getMessage());
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

    private void watch(TemplateInstance template) {

        try {
            //if timeout
            int timeout = LocalDateTime.now().compareTo(template.getCreatedAt().plusSeconds(templateDeploymentCacheApi.getTimeoutSeconds()));
            if (timeout > 0) {
                String timeoutMessage = CommonConstant.TIMEOUT_MESSAGE;
                PodList podList = podApi.readList(template.getNamespace(), Map.of("app", template.getName()));
                if (Objects.nonNull(podList) && !CollectionUtils.isEmpty(podList.getItems())) {
                    List<String> podNameList = podList.getItems()
                            .stream()
                            .map(e -> e.getMetadata().getName())
                            .collect(Collectors.toList());

                    timeoutMessage = podNameList.stream()
                            .map(pod -> kubectlApi.namespacedPodEvents(template.getNamespace(), pod))
                            .flatMap(Collection::stream)
                            .filter(event -> Objects.equals("Warning", event.getType()))
                            .map(Event::getMessage)
                            .distinct()
                            .collect(Collectors.joining("\n"));
                }

                template.setMessage(timeoutMessage);
                template.setSuccess(false);
                templateInstanceService.saveOrUpdate(template);

                Log.warn(TemplateRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's template [%s] is failed! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));

                return;
            }

            //deploying
            Deployment deployment = deploymentApi.read(template.getNamespace(), template.getName());
            boolean ready = TemplateInstanceDeploymentStatus.isReady(deployment);
            if (!ready) {
                Log.info(TemplateRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's template [%s] is not ready! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));
                return;
            }

            //deployment success
            String nodePorts;

            Service service = serviceApi.read(template.getNamespace(), template.getService());
            Assert.notNull(service, String.format("Read k8s service is null. namespace:%s, name:%s", template.getNamespace(), template.getName()));
            List<ServicePort> ports = service.getSpec().getPorts();
            if (!CollectionUtils.isEmpty(ports) && ports.size() > 1) {
                nodePorts = ports.stream()
                        .map(e -> String.valueOf(e.getNodePort()))
                        .collect(Collectors.joining(","));
            } else {
                nodePorts = String.valueOf(ports.get(0).getNodePort());
            }

            template.setNodePorts(nodePorts);
            template.setMessage(CommonConstant.SUCCESS_MESSAGE);
            template.setSuccess(true);
            templateInstanceService.saveOrUpdate(template);

            Log.info(TemplateRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's [%s] template [%s] deploy success.", template.getUser(), template.getName(), template.getId()));

            if (StringUtils.hasText(template.getIngress())) {
                List<HasMetadata> metadataList = kubectlApi.resourceListCreateOrReplace(template.getNamespace(), YamlBody.of(template.getIngress()));
                String ingress = metadataList.stream()
                        .map(e -> e.getMetadata().getName())
                        .findFirst().orElse(null);
                Log.info(TemplateRabbitMQK8sEventsListener.class.getName(), String.format("[%s] user's [%s] template ingress [%s] create success.", template.getUser(), template.getName(), ingress));
            }

        } catch (Exception e) {
            templateDeploymentCacheApi.unLock(template.getId());
            Log.error(TemplateRabbitMQK8sEventsListener.class.getName(), String.format("%s", e.getMessage()));
            template.setSuccess(false);
            template.setMessage(e.getMessage());
            templateInstanceService.saveOrUpdate(template);
        }
    }

}

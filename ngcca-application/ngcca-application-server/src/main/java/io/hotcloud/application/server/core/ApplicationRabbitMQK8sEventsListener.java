package io.hotcloud.application.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstancePlayer;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.api.exception.HotCloudException;
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
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@ConditionalOnProperty(
        name = ApplicationProperties.PROPERTIES_TYPE_NAME,
        havingValue = ApplicationProperties.RABBITMQ
)
@RequiredArgsConstructor
@Slf4j
public class ApplicationRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final ApplicationInstanceK8sService applicationInstanceK8sService;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationInstancePlayer applicationInstancePlayer;

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
//                Log.warn(ApplicationRabbitMQK8sEventsListener.class.getName(), "Get application null with id [" + businessId + "]. ignore this event");
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())){
                log.info("Application Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                applicationInstancePlayer.delete(businessId);
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())){
                if (fetched.isSuccess()) {
//                    log.info("Application [{}] {} events: {}/{}/{} ignore already success event", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                    return;
                }
                log.info("Application [{}] {} events: {}/{}/{}", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                applicationInstanceK8sService.processApplicationCreatedBlocked(fetched);
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
            throw new HotCloudException(e.getMessage());
        }
    }

}

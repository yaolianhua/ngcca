package io.hotcloud.server.application.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.common.utils.Log;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstancePlayer;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import io.hotcloud.server.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationInstancePlayer applicationInstancePlayer;
    private final ApplicationDeploymentWatchService applicationDeploymentWatchService;

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
            if (!Objects.equals(WorkloadsType.Deployment.name(), messageBody.getKind())) {
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)) {
                return;
            }
            ApplicationInstance fetched = applicationInstanceService.findOne(businessId);
            if (Objects.isNull(fetched)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())) {
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
                applicationDeploymentWatchService.mqWatch(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())) {
                log.info("Application error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        } catch (Exception e) {
            Log.error(ApplicationRabbitMQK8sEventsListener.class.getName(), e.getMessage());
        }

    }

    private Message<WatchMessageBody> convertWatchMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException(e.getMessage());
        }
    }

}

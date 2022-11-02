package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.message.Message;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.model.WatchMessageBody;
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
        name = BuildPackImagebuildProperties.PROPERTIES_TYPE_NAME,
        havingValue = BuildPackImagebuildProperties.RABBITMQ
)
@RequiredArgsConstructor
@Slf4j
public class BuildPackRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final BuildPackWatchService buildPackWatchService;
    private final BuildPackService buildPackService;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_JOB_BUILDPACK),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB)
                    )
            }
    )
    public void subscribe(String message) {

        try {
            WatchMessageBody messageBody = convertWatchMessageBody(message).getData();
            if (!Objects.equals(WorkloadsType.Job.name(), messageBody.getKind())){
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)){
                return;
            }
            BuildPack fetched = buildPackService.findByUuid(businessId);
            if (Objects.isNull(fetched)) {
//                Log.warn(BuildPackRabbitMQK8sEventsListener.class.getName(), "Get buildPack null with uuid [" + businessId + "]. ignore this event");
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())){
                log.info("BuildPack Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                buildPackWatchService.watchDeleted(fetched);
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())){
                if (fetched.isDone()) {
//                    log.info("BuildPack [{}] {} events: {}/{}/{} ignore already done event", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                    return;
                }
                log.info("BuildPack [{}] {} events: {}/{}/{}", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                buildPackWatchService.watchCreated(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())){
                log.info("BuildPack error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        }catch (Exception e){
            Log.error(BuildPackRabbitMQK8sEventsListener.class.getName(), e.getMessage());
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

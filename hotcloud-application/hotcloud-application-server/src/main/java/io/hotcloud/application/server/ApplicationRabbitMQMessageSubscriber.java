package io.hotcloud.application.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageProperties;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.UserNamespacePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@Slf4j
public class ApplicationRabbitMQMessageSubscriber {

    private final InstanceTemplatePlayer instanceTemplatePlayer;
    private final InstanceTemplateService instanceTemplateService;

    private final ObjectMapper objectMapper;

    public ApplicationRabbitMQMessageSubscriber(InstanceTemplatePlayer instanceTemplatePlayer,
                                                InstanceTemplateService instanceTemplateService,
                                                ObjectMapper objectMapper) {
        this.instanceTemplatePlayer = instanceTemplatePlayer;
        this.instanceTemplateService = instanceTemplateService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = SecurityConstant.QUEUE_APPLICATION_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = SecurityConstant.EXCHANGE_FANOUT_SECURITY_MESSAGE)
                    )
            }
    )
    public void userDeleted(String message) {

        try {
            Message<UserNamespacePair> messageBody = convertUserMessageBody(message);
            UserNamespacePair pair = messageBody.getData();
            log.info("[ApplicationRabbitMQMessageSubscriber] received [{}] user deleted message", pair.getUsername());
            List<InstanceTemplate> instanceTemplates = instanceTemplateService.findAll(pair.getUsername());
            for (InstanceTemplate template : instanceTemplates) {
                instanceTemplatePlayer.delete(template.getId());
            }
            log.info("[ApplicationRabbitMQMessageSubscriber] [{}] user {} instance template has been deleted", pair.getUsername(), instanceTemplates.size());
        } catch (Exception e) {
            log.info("[ApplicationRabbitMQMessageSubscriber] error. {}", e.getMessage());
        }


    }

    private Message<UserNamespacePair> convertUserMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }
}

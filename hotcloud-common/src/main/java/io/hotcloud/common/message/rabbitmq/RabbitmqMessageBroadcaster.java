package io.hotcloud.common.message.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageBroadcaster;
import io.hotcloud.common.message.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@Slf4j
public class RabbitmqMessageBroadcaster implements MessageBroadcaster {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitmqMessageBroadcaster(RabbitTemplate rabbitTemplate,
                                      ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> void broadcast(String exchange, Message<T> message) {
        try {
            String content = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(exchange, "", content);
            log.debug("Rabbitmq broadcast message: \n {}", content);
        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }

    }
}

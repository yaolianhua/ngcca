package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@Slf4j
public class RabbitmqMessageBroadcaster implements MessageBroadcaster {

    private final RabbitTemplate rabbitTemplate;

    public RabbitmqMessageBroadcaster(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public <T> void broadcast(Message<T> message) {
        rabbitTemplate.convertAndSend(RabbitmqConfiguration.EXCHANGE_FANOUT_BROADCAST_MESSAGE, "", message);
        log.debug("Rabbitmq broadcast message: \n {}", message);
    }
}

package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.MessageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(name = MessageProperties.TYPE_NAME, havingValue = MessageProperties.RABBITMQ)
public class RabbitMqMessageBroadcaster implements MessageBroadcaster {

    @Override
    public <T> void broadcast(Message<T> message) {

    }
}

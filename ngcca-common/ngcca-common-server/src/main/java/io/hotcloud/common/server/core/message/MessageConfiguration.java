package io.hotcloud.common.server.core.message;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.core.message.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MessageProperties.class)
@Slf4j
public class MessageConfiguration {

    private final MessageProperties properties;

    public MessageConfiguration(MessageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        MessageProperties.Type type = properties.getType();
        if (MessageProperties.Type.websocket.equals(type)) {
            Log.info(MessageConfiguration.class.getName(), "【Load Message Configuration. implementation using WebSocket】");
        } else if (MessageProperties.Type.rabbitmq.equals(type)) {
            MessageProperties.RabbitmqProperties rabbitmq = properties.getRabbitmq();
            Assert.notNull(rabbitmq, "Rabbitmq config is null");
            String rabbitmqUrl = String.format("amqp://%s@%s:%s", rabbitmq.getUsername(), rabbitmq.getHost(), rabbitmq.getPort());
            Log.info(MessageConfiguration.class.getName(), String.format("【Load Message Configuration. implementation using RabbitMQ. url='%s'】", rabbitmqUrl));
        }
    }

}

package io.hotcloud.common.server.message;

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
            log.info("【Load Message Configuration. implementation using WebSocket】");
        } else if (MessageProperties.Type.rabbitmq.equals(type)) {
            MessageProperties.RabbitmqProperties rabbitmq = properties.getRabbitmq();
            Assert.notNull(rabbitmq, "Rabbitmq config is null");
            log.info("【Load Message Configuration. implementation using RabbitMQ. url='{}'】",
                    String.format("amqp://%s@%s:%s", rabbitmq.getUsername(), rabbitmq.getHost(), rabbitmq.getPort()));
        }
    }

}

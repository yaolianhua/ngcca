package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.server.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
@ConditionalOnProperty(
        name = MessageProperties.TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@EnableConfigurationProperties(RabbitmqProperties.class)
@EnableRabbit
public class RabbitmqConfiguration {
}

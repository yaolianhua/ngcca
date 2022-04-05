package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.server.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@EnableRabbit
public class RabbitmqConfiguration {

    public static final String EXCHANGE_FANOUT_BROADCAST_MESSAGE = "hotcloud.message.broadcast";

    public static final String QUEUE_SUBSCRIBE_MESSAGE = "hotcloud.message.subscribe";

    @Bean
    public FanoutExchange messageBroadcastExchange() {
        return ExchangeBuilder.fanoutExchange(EXCHANGE_FANOUT_BROADCAST_MESSAGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue messageSubscribeQueue() {
        return QueueBuilder.durable(QUEUE_SUBSCRIBE_MESSAGE).build();
    }

    @Bean
    public Binding binding(FanoutExchange messageBroadcastExchange,
                           Queue messageSubscribeQueue) {
        return BindingBuilder.bind(messageSubscribeQueue)
                .to(messageBroadcastExchange);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory,
                                   FanoutExchange messageBroadcastExchange,
                                   Queue messageSubscribeQueue) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(messageSubscribeQueue);
        log.info("Created default queue: {}", QUEUE_SUBSCRIBE_MESSAGE);
        rabbitAdmin.declareExchange(messageBroadcastExchange);
        log.info("Created default exchange: {}", EXCHANGE_FANOUT_BROADCAST_MESSAGE);
        return rabbitAdmin;
    }

}

package io.hotcloud.common.server.core.message.rabbitmq;

import io.hotcloud.common.api.Log;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableRabbit
public class RabbitmqConfiguration {

    public static final String EXCHANGE_FANOUT_BROADCAST_MESSAGE = "ngcca.message.broadcast";

    public static final String QUEUE_SUBSCRIBE_MESSAGE = "ngcca.message.subscribe";

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
        Log.info(RabbitmqConfiguration.class.getName(), String.format("Created default queue [%s]", QUEUE_SUBSCRIBE_MESSAGE));
        rabbitAdmin.declareExchange(messageBroadcastExchange);
        Log.info(RabbitmqConfiguration.class.getName(), String.format("Created default exchange [%s]", EXCHANGE_FANOUT_BROADCAST_MESSAGE));
        return rabbitAdmin;
    }

}

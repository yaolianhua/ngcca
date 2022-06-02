package io.hotcloud.security.server.user;

import io.hotcloud.common.api.message.MessageProperties;
import io.hotcloud.security.SecurityRunnerProcessor;
import io.hotcloud.security.api.SecurityConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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
class SecurityUserRabbitMqRunnerProcessor implements SecurityRunnerProcessor {

    private final RabbitAdmin rabbitAdmin;

    public SecurityUserRabbitMqRunnerProcessor(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void execute() {
        Queue applicationQueue = QueueBuilder.durable(SecurityConstant.QUEUE_APPLICATION_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE).build();
        Queue buildPackQueue = QueueBuilder.durable(SecurityConstant.QUEUE_BUILDPACK_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE).build();
        FanoutExchange exchange = ExchangeBuilder.fanoutExchange(SecurityConstant.EXCHANGE_FANOUT_SECURITY_MESSAGE).build();
        Binding applicationBinding = BindingBuilder.bind(applicationQueue).to(exchange);
        Binding buildPackBinding = BindingBuilder.bind(buildPackQueue).to(exchange);

        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(applicationQueue);
        rabbitAdmin.declareQueue(buildPackQueue);
        rabbitAdmin.declareBinding(applicationBinding);
        rabbitAdmin.declareBinding(buildPackBinding);

    }
}

package io.hotcloud.security.server.user;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.message.MessageProperties;
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
class SecurityUserRabbitMqRunnerProcessor implements CommonRunnerProcessor {

    private final RabbitAdmin rabbitAdmin;

    public SecurityUserRabbitMqRunnerProcessor(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void execute() {
        Queue queue = QueueBuilder.durable(CommonConstant.MQ_QUEUE_SECURITY_USER_DELETE).build();

        FanoutExchange exchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_SECURITY_MODULE).build();

        Binding binding = BindingBuilder.bind(queue).to(exchange);

        rabbitAdmin.declareExchange(exchange);

        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(binding);

    }
}

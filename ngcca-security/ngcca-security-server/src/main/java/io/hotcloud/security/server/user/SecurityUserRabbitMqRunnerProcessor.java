package io.hotcloud.security.server.user;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.CommonRunnerProcessor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

@Component
class SecurityUserRabbitMqRunnerProcessor implements CommonRunnerProcessor {

    private final ConnectionFactory connectionFactory;

    public SecurityUserRabbitMqRunnerProcessor(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void execute() {
        Queue queue = QueueBuilder.durable(CommonConstant.MQ_QUEUE_SECURITY_USER_DELETE).build();

        FanoutExchange exchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_SECURITY_MODULE).build();

        Binding binding = BindingBuilder.bind(queue).to(exchange);

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);

        rabbitAdmin.declareExchange(exchange);

        rabbitAdmin.declareQueue(queue);

        rabbitAdmin.declareBinding(binding);

    }
}

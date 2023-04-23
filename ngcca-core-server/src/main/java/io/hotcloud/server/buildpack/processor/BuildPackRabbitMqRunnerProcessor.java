package io.hotcloud.server.buildpack.processor;

import io.hotcloud.module.buildpack.BuildPackConstant;
import io.hotcloud.server.NGCCARunnerProcessor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

@Component
class BuildPackRabbitMqRunnerProcessor implements NGCCARunnerProcessor {

    private final RabbitAdmin rabbitAdmin;

    public BuildPackRabbitMqRunnerProcessor(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void execute() {
        Queue queue = QueueBuilder.durable(BuildPackConstant.QUEUE_SUBSCRIBE_BUILDPACK_DONE_MESSAGE).build();
        FanoutExchange exchange = ExchangeBuilder.fanoutExchange(BuildPackConstant.EXCHANGE_FANOUT_BUILDPACK_MESSAGE).build();
        Binding binding = BindingBuilder.bind(queue).to(exchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);

    }
}

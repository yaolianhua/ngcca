package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.core.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.common.message.MessageProperties;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class BuildPackApplicationRabbitMQPostProcessor implements BuildPackApplicationRunnerPostProcessor {

    private final RabbitAdmin rabbitAdmin;

    public BuildPackApplicationRabbitMQPostProcessor(RabbitAdmin rabbitAdmin) {
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

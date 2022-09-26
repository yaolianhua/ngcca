package io.hotcloud.kubernetes.server;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.message.MessageProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
public class KubernetesRabbitMQRunnerProcessor implements CommonRunnerProcessor {

    private final RabbitAdmin rabbitAdmin;

    public KubernetesRabbitMQRunnerProcessor(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void execute() {
        Queue queue = QueueBuilder.durable(CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_EVENTS).build();
        Queue jobQueue = QueueBuilder.durable(CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_JOB).build();

        FanoutExchange cronjobExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_CRONJOB).build();
        FanoutExchange jobExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB).build();
        FanoutExchange deploymentExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT).build();
        FanoutExchange daemonsetExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DAEMONSET).build();
        FanoutExchange statefulsetExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_STATEFULSET).build();
        FanoutExchange podExchange = ExchangeBuilder.fanoutExchange(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_POD).build();

        Binding cronjobBinding = BindingBuilder.bind(queue).to(cronjobExchange);
        Binding jobBinding = BindingBuilder.bind(queue).to(jobExchange);
        Binding deploymentBinding = BindingBuilder.bind(queue).to(deploymentExchange);
        Binding daemonsetBinding = BindingBuilder.bind(queue).to(daemonsetExchange);
        Binding statefulsetBinding = BindingBuilder.bind(queue).to(statefulsetExchange);
        Binding podBinding = BindingBuilder.bind(queue).to(podExchange);

        Binding jobQueueBinding = BindingBuilder.bind(jobQueue).to(jobExchange);

        rabbitAdmin.declareExchange(cronjobExchange);
        rabbitAdmin.declareExchange(jobExchange);
        rabbitAdmin.declareExchange(deploymentExchange);
        rabbitAdmin.declareExchange(daemonsetExchange);
        rabbitAdmin.declareExchange(statefulsetExchange);
        rabbitAdmin.declareExchange(podExchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareQueue(jobQueue);

        rabbitAdmin.declareBinding(cronjobBinding);
        rabbitAdmin.declareBinding(jobBinding);
        rabbitAdmin.declareBinding(deploymentBinding);
        rabbitAdmin.declareBinding(daemonsetBinding);
        rabbitAdmin.declareBinding(statefulsetBinding);
        rabbitAdmin.declareBinding(podBinding);

        rabbitAdmin.declareBinding(jobQueueBinding);


    }
}

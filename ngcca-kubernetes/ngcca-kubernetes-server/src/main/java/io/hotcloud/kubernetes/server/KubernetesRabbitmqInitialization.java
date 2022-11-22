package io.hotcloud.kubernetes.server;

import io.hotcloud.kubernetes.model.module.RabbitMQConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kubernetes.enable-workloads-watcher", havingValue = "true")
public class KubernetesRabbitmqInitialization implements ApplicationRunner {

    private final ConnectionFactory connectionFactory;

    public KubernetesRabbitmqInitialization(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Queue queue = QueueBuilder.durable(RabbitMQConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_EVENTS).build();
        Queue buildPackJobQueue = QueueBuilder.durable(RabbitMQConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_JOB_BUILDPACK).build();
        Queue applicationDeploymentQueue = QueueBuilder.durable(RabbitMQConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_APPLICATION).build();
        Queue templateDeploymentQueue = QueueBuilder.durable(RabbitMQConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_TEMPLATE).build();

        FanoutExchange cronjobExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_CRONJOB).build();
        FanoutExchange jobExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB).build();
        FanoutExchange deploymentExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT).build();
        FanoutExchange daemonsetExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DAEMONSET).build();
        FanoutExchange statefulsetExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_STATEFULSET).build();
        FanoutExchange podExchange = ExchangeBuilder.fanoutExchange(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_POD).build();

        Binding cronjobBinding = BindingBuilder.bind(queue).to(cronjobExchange);
        Binding jobBinding = BindingBuilder.bind(queue).to(jobExchange);
        Binding deploymentBinding = BindingBuilder.bind(queue).to(deploymentExchange);
        Binding daemonsetBinding = BindingBuilder.bind(queue).to(daemonsetExchange);
        Binding statefulsetBinding = BindingBuilder.bind(queue).to(statefulsetExchange);
        Binding podBinding = BindingBuilder.bind(queue).to(podExchange);

        Binding jobQueueBinding = BindingBuilder.bind(buildPackJobQueue).to(jobExchange);
        Binding deploymentQueueBinding = BindingBuilder.bind(applicationDeploymentQueue).to(deploymentExchange);
        Binding templateDeploymentQueueBinding = BindingBuilder.bind(templateDeploymentQueue).to(deploymentExchange);

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareExchange(cronjobExchange);
        rabbitAdmin.declareExchange(jobExchange);
        rabbitAdmin.declareExchange(deploymentExchange);
        rabbitAdmin.declareExchange(daemonsetExchange);
        rabbitAdmin.declareExchange(statefulsetExchange);
        rabbitAdmin.declareExchange(podExchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareQueue(buildPackJobQueue);
        rabbitAdmin.declareQueue(applicationDeploymentQueue);
        rabbitAdmin.declareQueue(templateDeploymentQueue);

        rabbitAdmin.declareBinding(cronjobBinding);
        rabbitAdmin.declareBinding(jobBinding);
        rabbitAdmin.declareBinding(deploymentBinding);
        rabbitAdmin.declareBinding(daemonsetBinding);
        rabbitAdmin.declareBinding(statefulsetBinding);
        rabbitAdmin.declareBinding(podBinding);

        rabbitAdmin.declareBinding(jobQueueBinding);
        rabbitAdmin.declareBinding(deploymentQueueBinding);
        rabbitAdmin.declareBinding(templateDeploymentQueueBinding);


    }
}

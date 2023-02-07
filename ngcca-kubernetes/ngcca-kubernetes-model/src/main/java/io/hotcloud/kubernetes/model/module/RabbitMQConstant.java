package io.hotcloud.kubernetes.model.module;

public final class RabbitMQConstant {

    public static final String MQ_QUEUE_KUBERNETES_WORKLOADS_EVENTS = "queue-hotcloud.message.kubernetes.workloads.events";
    public static final String MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_TEMPLATE = "queue-hotcloud.message.template.instance.deployment";
    public static final String MQ_QUEUE_KUBERNETES_WORKLOADS_JOB_BUILDPACK = "queue-hotcloud.message.buildpack.job";
    public static final String MQ_QUEUE_KUBERNETES_WORKLOADS_DEPLOYMENT_APPLICATION = "queue-hotcloud.message.application.instance.deployment";
    public static final String MQ_QUEUE_KUBERNETES_CLUSTER_AGENT = "queue-hotcloud.message.cluster.agent";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_CLUSTER_AGENT = "exchange-hotcloud.message.cluster.agent";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT = "exchange-hotcloud.message.workloads.deployment";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB = "exchange-hotcloud.message.workloads.job";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DAEMONSET = "exchange-hotcloud.message.workloads.daemonset";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_CRONJOB = "exchange-hotcloud.message.workloads.cronjob";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_STATEFULSET = "exchange-hotcloud.message.workloads.statefulset";
    public static final String MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_POD = "exchange-hotcloud.message.workloads.pod";
}

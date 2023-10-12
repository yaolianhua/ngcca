package io.hotcloud.service.cluster.statistic;

public enum WorkloadObjectType {

    DEPLOYMENT,
    POD,
    STATEFULSET,
    DAEMONSET,
    JOB,
    CRONJOB,
    SERVICE,
    CONFIGMAP,
    SECRET,
    INGRESS
}

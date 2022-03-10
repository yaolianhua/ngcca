package io.hotcloud.kubernetes.api.workload;

import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.LabelSelectorBuilder;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.api.pod.PodTemplateSpecBuilder;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Strategy;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.hotcloud.kubernetes.model.workload.DaemonSetSpec;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class DaemonSetBuilder {

    public static String API_VERSION = "apps/v1";
    public static String KIND = "DaemonSet";

    private DaemonSetBuilder() {
    }

    public static V1DaemonSet build(DaemonSetCreateRequest request) {

        V1DaemonSet v1Deployment = new V1DaemonSet();

        v1Deployment.setApiVersion(API_VERSION);
        v1Deployment.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = build(request.getMetadata());
        v1Deployment.setMetadata(v1ObjectMeta);

        V1DaemonSetSpec v1DeploymentSpec = build(request.getSpec());
        v1Deployment.setSpec(v1DeploymentSpec);

        return v1Deployment;
    }

    public static V1DaemonSetSpec build(DaemonSetSpec daemonSetSpec) {

        V1DaemonSetSpec spec = new V1DaemonSetSpec();

        //build Strategy
        V1DaemonSetUpdateStrategy v1DaemonSetUpdateStrategy = build(daemonSetSpec.getStrategy());
        spec.setUpdateStrategy(v1DaemonSetUpdateStrategy);

        //build selector
        V1LabelSelector v1LabelSelector = LabelSelectorBuilder.build(daemonSetSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        ObjectMetadata podTemplateMetadata = daemonSetSpec.getTemplate().getMetadata();
        PodTemplateSpec podTemplateSpec = daemonSetSpec.getTemplate().getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = PodTemplateSpecBuilder.build(podTemplateMetadata, podTemplateSpec, WorkloadsType.DaemonSet);
        spec.setTemplate(v1PodTemplateSpec);

        spec.setMinReadySeconds(daemonSetSpec.getMinReadySeconds());
        spec.setRevisionHistoryLimit(daemonSetSpec.getRevisionHistoryLimit());

        return spec;
    }

    private static V1ObjectMeta build(ObjectMetadata daemonSetMetadata) {
        String name = daemonSetMetadata.getName();
        String namespace = daemonSetMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "DaemonSet name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "DaemonSet namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(daemonSetMetadata.getLabels());
        v1ObjectMeta.setAnnotations(daemonSetMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }


    private static V1DaemonSetUpdateStrategy build(Strategy daemonSetUpdateStrategy) {

        V1DaemonSetUpdateStrategy strategy = new V1DaemonSetUpdateStrategy();
        strategy.setType(daemonSetUpdateStrategy.getType().name());

        Strategy.RollingUpdate rollingUpdate = daemonSetUpdateStrategy.getRollingUpdate();
        if (null != rollingUpdate) {
            V1RollingUpdateDaemonSet v1RollingUpdateDaemonSet = new V1RollingUpdateDaemonSet();
            v1RollingUpdateDaemonSet.setMaxUnavailable(new IntOrString(rollingUpdate.getMaxUnavailable()));
            strategy.setRollingUpdate(v1RollingUpdateDaemonSet);
        }

        return strategy;
    }
}

package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelectorBuilder;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Strategy;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpecBuilder;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class DeploymentBuilder {
    private DeploymentBuilder() {
    }

    public static String API_VERSION = "apps/v1";
    public static String KIND = "Deployment";

    public static V1Deployment build(DeploymentCreateRequest request) {

        V1Deployment v1Deployment = new V1Deployment();

        v1Deployment.setApiVersion(API_VERSION);
        v1Deployment.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = build(request.getMetadata());
        v1Deployment.setMetadata(v1ObjectMeta);

        V1DeploymentSpec v1DeploymentSpec = build(request.getSpec());
        v1Deployment.setSpec(v1DeploymentSpec);

        return v1Deployment;
    }

    public static V1DeploymentSpec build(DeploymentSpec deploymentSpec) {

        V1DeploymentSpec spec = new V1DeploymentSpec();

        //build v1DeploymentStrategy
        V1DeploymentStrategy v1DeploymentStrategy = build(deploymentSpec.getStrategy());
        spec.setStrategy(v1DeploymentStrategy);

        //build selector
        V1LabelSelector v1LabelSelector = LabelSelectorBuilder.build(deploymentSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        ObjectMetadata podTemplateMetadata = deploymentSpec.getTemplate().getMetadata();
        PodTemplateSpec podTemplateSpec = deploymentSpec.getTemplate().getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = PodTemplateSpecBuilder.build(podTemplateMetadata, podTemplateSpec, WorkloadsType.Deployment);
        spec.setTemplate(v1PodTemplateSpec);


        spec.setReplicas(deploymentSpec.getReplicas());
        spec.setMinReadySeconds(deploymentSpec.getMinReadySeconds());
        spec.setPaused(deploymentSpec.getPaused());
        spec.setProgressDeadlineSeconds(deploymentSpec.getProgressDeadlineSeconds());
        spec.setRevisionHistoryLimit(deploymentSpec.getRevisionHistoryLimit());

        return spec;
    }

    private static V1ObjectMeta build(ObjectMetadata deploymentMetadata) {
        String name = deploymentMetadata.getName();
        String namespace = deploymentMetadata.getNamespace();
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(deploymentMetadata.getLabels());
        v1ObjectMeta.setAnnotations(deploymentMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }


    private static V1DeploymentStrategy build(Strategy deploymentStrategy) {

        V1DeploymentStrategy strategy = new V1DeploymentStrategy();
        strategy.setType(deploymentStrategy.getType().name());

        Strategy.RollingUpdate rollingUpdate = deploymentStrategy.getRollingUpdate();
        if (null != rollingUpdate) {
            V1RollingUpdateDeployment rollingUpdateDeployment = new V1RollingUpdateDeployment();
            rollingUpdateDeployment.setMaxSurge(new IntOrString(rollingUpdate.getMaxSurge()));
            rollingUpdateDeployment.setMaxUnavailable(new IntOrString(rollingUpdate.getMaxUnavailable()));
            strategy.setRollingUpdate(rollingUpdateDeployment);
        }

        return strategy;
    }


}

package io.hotCloud.core.kubernetes.deploy;

import io.hotCloud.core.common.Assert;
import io.hotCloud.core.kubernetes.LabelSelectorBuilder;
import io.hotCloud.core.kubernetes.pod.PodTemplateMetadata;
import io.hotCloud.core.kubernetes.pod.PodTemplateSpec;
import io.hotCloud.core.kubernetes.pod.PodTemplateSpecBuilder;
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

    public static V1Deployment buildV1Deployment(DeploymentCreateParams request) {

        V1Deployment v1Deployment = new V1Deployment();

        v1Deployment.setApiVersion(API_VERSION);
        v1Deployment.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = buildV1DeploymentMetadata(request.getMetadata());
        v1Deployment.setMetadata(v1ObjectMeta);

        V1DeploymentSpec v1DeploymentSpec = buildV1DeploymentSpec(request.getSpec());
        v1Deployment.setSpec(v1DeploymentSpec);

        return v1Deployment;
    }

    public static V1DeploymentSpec buildV1DeploymentSpec(DeploymentSpec deploymentSpec){

        V1DeploymentSpec spec = new V1DeploymentSpec();

        //build v1DeploymentStrategy
        V1DeploymentStrategy v1DeploymentStrategy = buildV1DeploymentStrategy(deploymentSpec.getStrategy());
        spec.setStrategy(v1DeploymentStrategy);

        //build selector
        V1LabelSelector v1LabelSelector = LabelSelectorBuilder.build(deploymentSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        PodTemplateMetadata podTemplateMetadata = deploymentSpec.getTemplate().getMetadata();
        PodTemplateSpec podTemplateSpec = deploymentSpec.getTemplate().getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = PodTemplateSpecBuilder.build(podTemplateMetadata, podTemplateSpec);
        spec.setTemplate(v1PodTemplateSpec);


        spec.setReplicas(deploymentSpec.getReplicas());
        spec.setMinReadySeconds(deploymentSpec.getMinReadySeconds());
        spec.setPaused(deploymentSpec.isPaused());
        spec.setProgressDeadlineSeconds(deploymentSpec.getProgressDeadlineSeconds());
        spec.setRevisionHistoryLimit(deploymentSpec.getRevisionHistoryLimit());

        return spec;
    }

    private static V1ObjectMeta buildV1DeploymentMetadata(DeploymentMetadata deploymentMetadata){
        String name = deploymentMetadata.getName();
        String namespace = deploymentMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "Deployment name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "Deployment namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(deploymentMetadata.getLabels());
        v1ObjectMeta.setAnnotations(deploymentMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }


    private static V1DeploymentStrategy buildV1DeploymentStrategy(DeploymentStrategy deploymentStrategy){

        V1DeploymentStrategy strategy = new V1DeploymentStrategy();
        strategy.setType(deploymentStrategy.getType().name());

        DeploymentStrategy.RollingUpdate rollingUpdate = deploymentStrategy.getRollingUpdate();
        if (null != rollingUpdate){
            V1RollingUpdateDeployment rollingUpdateDeployment = new V1RollingUpdateDeployment();
            rollingUpdateDeployment.setMaxSurge(new IntOrString(rollingUpdate.getMaxSurge()));
            rollingUpdateDeployment.setMaxUnavailable(new IntOrString(rollingUpdate.getMaxUnavailable()));
            strategy.setRollingUpdate(rollingUpdateDeployment);
        }

        return strategy;
    }


}

package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.application.api.core.ApplicationInstanceSource;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.Strategy;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.*;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.hotcloud.kubernetes.model.workload.DeploymentSpec;
import io.hotcloud.kubernetes.model.workload.DeploymentTemplate;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static io.hotcloud.common.api.CommonConstant.K8S_APP;

@Component
@RequiredArgsConstructor
@Order(10)
class ApplicationInstanceDeploymentProcessor implements ApplicationInstanceProcessor <ApplicationInstance, Void> {

    private final DeploymentApi deploymentApi;
    private final BuildPackService buildPackService;
    private final ApplicationInstanceService applicationInstanceService;

    private ObjectMetadata buildDeploymentMetadata(ApplicationInstance applicationInstance){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(applicationInstance.getName());
        metadata.setNamespace(applicationInstance.getNamespace());

        return metadata;
    }

    private DeploymentTemplate buildDeploymentTemplate(ApplicationInstance applicationInstance){
        DeploymentTemplate template = new DeploymentTemplate();
        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(Map.of(K8S_APP, applicationInstance.getName()));
        template.setMetadata(templateMetadata);

        PodTemplateSpec podTemplateSpec = new PodTemplateSpec();

        String imageUrl;
        if (ApplicationInstanceSource.Origin.IMAGE.equals(applicationInstance.getSource().getOrigin())) {
            imageUrl = applicationInstance.getSource().getUrl();
        }else {
            BuildPack buildPack = buildPackService.findOne(applicationInstance.getBuildPackId());
            Assert.notNull(buildPack, "Can not found buildPack object [" + applicationInstance.getBuildPackId() + "]");
            Assert.isTrue(buildPack.isDone() && Objects.equals(buildPack.getMessage(), CommonConstant.SUCCESS_MESSAGE), "BuildPack status is wrong. it does not done or not success");
            Assert.hasText(buildPack.getArtifact(), "Get image url is null. [" + buildPack.getId() + "]");
            imageUrl = buildPack.getArtifact();
        }
        Container container = this.buildContainer(applicationInstance, imageUrl);

        podTemplateSpec.setContainers(List.of(container));
        template.setSpec(podTemplateSpec);

        return template;
    }

    private DeploymentSpec buildDeploymentSpec (ApplicationInstance applicationInstance){
        DeploymentSpec deploymentSpec = new DeploymentSpec();

        deploymentSpec.setReplicas(applicationInstance.getReplicas());
        deploymentSpec.setStrategy(new Strategy());
        deploymentSpec.setSelector(new LabelSelector(Map.of(K8S_APP, applicationInstance.getName()), Collections.emptyList()));

        deploymentSpec.setTemplate(buildDeploymentTemplate(applicationInstance));

        return deploymentSpec;
    }
    @Override
    public Void process(ApplicationInstance applicationInstance) {

        DeploymentCreateRequest request = new DeploymentCreateRequest();

        request.setMetadata(buildDeploymentMetadata(applicationInstance));
        request.setSpec(buildDeploymentSpec(applicationInstance));

        try {
            deploymentApi.deployment(request);
        } catch (ApiException e) {
            applicationInstance.setMessage(e.getMessage());
        }

        ApplicationInstance saved = applicationInstanceService.saveOrUpdate(applicationInstance);
        Log.info(ApplicationInstanceDeploymentProcessor.class.getName(), String.format("[%s] user's application instance [%s] created, id [%s]", saved.getUser(), saved.getName(), saved.getId()));
        return null;
    }

    private Container buildContainer(ApplicationInstance applicationInstance, String imageUrl){

        Container container = new Container();
        container.setName(applicationInstance.getName());
        List<Env> envs = applicationInstance.getEnvs().entrySet()
                .stream()
                .map(e -> {
                    Env env = new Env();
                    env.setName(e.getKey());
                    env.setValue(e.getValue());
                    return env;
                })
                .collect(Collectors.toList());
        container.setEnv(envs);
        container.setImage(imageUrl);
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);
        List<Port> ports = new ArrayList<>();
        for (String targetPort : applicationInstance.getTargetPorts().split(",")) {
            Port port = new Port();
            port.setContainerPort(Integer.parseInt(targetPort));
            port.setProtocol(PortProtocol.TCP);
            ports.add(port);
        }
        container.setPorts(ports);
        Resources resources = new Resources();
        resources.setRequests(Map.of("cpu", "100m", "memory", "128Mi"));
        resources.setLimits(Map.of("cpu", "1000m", "memory", "1024Mi"));
        container.setResources(resources);


        return container;
    }
}

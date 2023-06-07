package io.hotcloud.service.application;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.Strategy;
import io.hotcloud.kubernetes.model.pod.ImagePullSecret;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.*;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.hotcloud.kubernetes.model.workload.DeploymentSpec;
import io.hotcloud.kubernetes.model.workload.DeploymentTemplate;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstanceProcessor;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import io.hotcloud.module.application.core.ApplicationInstanceSource;
import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildPack;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.hotcloud.common.model.CommonConstant.K8S_APP;

@Component
@RequiredArgsConstructor
class ApplicationInstanceDeploymentProcessor implements ApplicationInstanceProcessor<ApplicationInstance> {

    private final DeploymentClient deploymentApi;
    private final BuildPackService buildPackService;
    private final ApplicationInstanceService applicationInstanceService;

    @Override
    public int order() {
        return DEFAULT_ORDER + 10;
    }

    @Override
    public Type getType() {
        return Type.Deployment;
    }

    private ObjectMetadata buildDeploymentMetadata(ApplicationInstance applicationInstance) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(applicationInstance.getName());
        metadata.setNamespace(applicationInstance.getNamespace());
        metadata.setLabels(Map.of(CommonConstant.K8S_APP_BUSINESS_DATA_ID, applicationInstance.getId(), K8S_APP, applicationInstance.getName()));

        return metadata;
    }

    private DeploymentTemplate buildDeploymentTemplate(ApplicationInstance applicationInstance) {
        DeploymentTemplate template = new DeploymentTemplate();
        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(Map.of(CommonConstant.K8S_APP_BUSINESS_DATA_ID, applicationInstance.getId(), K8S_APP, applicationInstance.getName()));
        template.setMetadata(templateMetadata);

        PodTemplateSpec podTemplateSpec = new PodTemplateSpec();

        String imageUrl = retrieveApplicationInstanceImageUrl(applicationInstance);
        Container container = this.buildContainer(applicationInstance, imageUrl);

        podTemplateSpec.setContainers(List.of(container));

        String secret = retrieveApplicationInstanceImagePullSecret(applicationInstance);
        podTemplateSpec.setImagePullSecrets(StringUtils.hasText(secret) ? List.of(new ImagePullSecret(secret)) : List.of());

        template.setSpec(podTemplateSpec);

        return template;
    }

    private String retrieveApplicationInstanceImageUrl(ApplicationInstance applicationInstance) {

        if (ApplicationInstanceSource.Origin.IMAGE.equals(applicationInstance.getSource().getOrigin())) {
            return applicationInstance.getSource().getUrl();
        } else {
            BuildPack buildPack = buildPackService.findOne(applicationInstance.getBuildPackId());
            Assert.notNull(buildPack, "Can not found buildPack object [" + applicationInstance.getBuildPackId() + "]");
            Assert.isTrue(buildPack.isDone() && Objects.equals(buildPack.getMessage(), CommonConstant.SUCCESS_MESSAGE), "BuildPack status is wrong. it does not done or not success");
            Assert.hasText(buildPack.getArtifact(), "BuildPack is success, but get image url is null. [" + buildPack.getId() + "]");
            return buildPack.getArtifact();
        }
    }

    private String retrieveApplicationInstanceImagePullSecret(ApplicationInstance applicationInstance) {

        if (ApplicationInstanceSource.Origin.IMAGE.equals(applicationInstance.getSource().getOrigin())) {
            return null;
        } else {
            BuildPack buildPack = buildPackService.findOne(applicationInstance.getBuildPackId());
            Assert.notNull(buildPack, "Can not found buildPack object [" + applicationInstance.getBuildPackId() + "]");
            return buildPack.getSecretResource().getName();
        }
    }

    private DeploymentSpec buildDeploymentSpec(ApplicationInstance applicationInstance) {
        DeploymentSpec deploymentSpec = new DeploymentSpec();

        deploymentSpec.setReplicas(applicationInstance.getReplicas());
        deploymentSpec.setStrategy(new Strategy());
        deploymentSpec.setSelector(new LabelSelector(Map.of(CommonConstant.K8S_APP_BUSINESS_DATA_ID, applicationInstance.getId(), K8S_APP, applicationInstance.getName()), Collections.emptyList()));

        deploymentSpec.setTemplate(buildDeploymentTemplate(applicationInstance));

        return deploymentSpec;
    }

    @SneakyThrows
    @Override
    public void processCreate(ApplicationInstance applicationInstance) {

        try {
            DeploymentCreateRequest request = new DeploymentCreateRequest();

            request.setMetadata(buildDeploymentMetadata(applicationInstance));
            request.setSpec(buildDeploymentSpec(applicationInstance));
            deploymentApi.create(request);
            Log.info(this, null, String.format("[%s] user's application instance k8s deployment [%s] created", applicationInstance.getUser(), applicationInstance.getName()));
        } catch (Exception e) {
            Log.error(this, null, String.format("[%s] user's application instance k8s deployment [%s] create error [%s]", applicationInstance.getUser(), applicationInstance.getName(), e.getMessage()));
            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            throw e;
        }

    }

    @SneakyThrows
    @Override
    public void processDelete(ApplicationInstance input) {
        Deployment deployment = deploymentApi.read(input.getNamespace(), input.getName());
        if (Objects.nonNull(deployment)) {
            deploymentApi.delete(input.getNamespace(), input.getName());
            Log.info(this, null,
                    String.format("[%s] user's application instance  k8s deployment [%s] deleted", input.getUser(), input.getName()));
        }

    }

    private Container buildContainer(ApplicationInstance applicationInstance, String imageUrl) {

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
        resources.setLimits(Map.of("cpu", "1000m", "memory", "4096Mi"));
        container.setResources(resources);


        return container;
    }
}

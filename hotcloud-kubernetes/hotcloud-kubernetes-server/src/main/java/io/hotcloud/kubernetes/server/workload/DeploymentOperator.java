package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.TimeoutImageEditReplacePatchable;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static io.hotcloud.common.UUIDGenerator.DEFAULT;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentOperator implements DeploymentApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DeploymentOperator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Deployment deployment(String yaml) throws ApiException {
        V1Deployment v1Deployment;
        try {
            v1Deployment = Yaml.loadAs(yaml, V1Deployment.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load deployment yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Deployment.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT;
        V1Deployment created = appsV1Api.createNamespacedDeployment(namespace,
                v1Deployment,
                "true",
                null,
                null, null);
        log.debug("create deployment success \n '{}'", created);

        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(v1Deployment.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String deployment) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(deployment, () -> "delete resource name is null");
        V1Status v1Status = appsV1Api.deleteNamespacedDeployment(
                deployment,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        log.debug("delete namespaced deployment success \n '{}'", v1Status);
    }

    @Override
    public DeploymentList read(String namespace, Map<String, String> labelSelector) {

        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.apps()
                    .deployments()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.apps()
                .deployments()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void scale(String namespace,
                      String deployment,
                      Integer count,
                      boolean wait) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(deployment, () -> "deployment name is null");
        Assert.state(Objects.nonNull(count), () -> "scale count is null");

        fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .scale(count, wait);

        log.debug("Deployment '{}' scaled to num of '{}'", deployment, count);
        if (wait) {
            log.debug("wait for the number of instances to exist - no guarantee is made as to readiness ");
        }

    }

    @Override
    public Deployment rolling(RollingAction action, String namespace, String deployment) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(deployment, () -> "deployment name is null");

        TimeoutImageEditReplacePatchable<Deployment> patchable = fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling();
        log.debug("Namespaced '{}' Deployment '{}' patched [{}]", namespace, deployment, action);
        switch (action) {
            case PAUSE:
                return patchable.pause();

            case RESUME:
                return patchable.resume();

            case RESTART:
                return patchable.restart();

            case UNDO:
                return patchable.undo();

            default:
                return null;
        }

    }

    @Override
    public Deployment imageUpdate(Map<String, String> containerImage, String namespace, String deployment) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(deployment, () -> "deployment name is null");
        Assert.state(!CollectionUtils.isEmpty(containerImage), () -> "containerImage map is empty");

        log.debug("Namespaced '{}' Deployment '{}' image patched '{}'", namespace, deployment, containerImage);
        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling()
                .updateImage(containerImage);


    }

    @Override
    public Deployment imageUpdate(String namespace, String deployment, String image) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(deployment, () -> "deployment name is null");
        Assert.hasText(image, () -> "image name is null");

        log.debug("Namespaced '{}' Deployment '{}' image patched '{}'", namespace, deployment, image);
        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling()
                .updateImage(image);

    }
}

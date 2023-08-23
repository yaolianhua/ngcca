package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.TimeoutImageEditReplacePatchable;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.DeploymentApi;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.hotcloud.kubernetes.model.RollingAction;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class DeploymentOperator implements DeploymentApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DeploymentOperator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Deployment create(String yaml) throws ApiException {
        V1Deployment v1Deployment;
        try {
            v1Deployment = Yaml.loadAs(yaml, V1Deployment.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load deployment yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Deployment.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1Deployment created = appsV1Api.createNamespacedDeployment(namespace,
                v1Deployment,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create deployment '%s' success", Objects.requireNonNull(created.getMetadata()).getName()));

        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(v1Deployment.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String deployment) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        Assert.hasText(deployment, () -> "delete resource name is null");
       appsV1Api.deleteNamespacedDeployment(
                deployment,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        Log.debug(this, null, String.format("delete '%s' namespaced deployment '%s' success", namespace, deployment));
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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.state(Objects.nonNull(count), () -> "scale count is null");

        fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .scale(count, wait);

        Log.debug(this, null, String.format("Deployment '%s' scaled to num of '%s'", deployment, count));
        if (wait) {
            Log.debug(this, null, "wait for the number of instances to exist - no guarantee is made as to readiness ");
        }

    }

    @Override
    public Deployment rolling(RollingAction action, String namespace, String deployment) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);

        TimeoutImageEditReplacePatchable<Deployment> patchable = fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling();
        Log.debug(this, null, String.format("'%s' Namespaced Deployment '%s' patched [%s]", namespace, deployment, action));
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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.state(!CollectionUtils.isEmpty(containerImage), () -> "containerImage map is empty");

        Log.debug(this, null, String.format("'%s' Namespaced Deployment '%s' image patched '%s'", namespace, deployment, containerImage));
        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling()
                .updateImage(containerImage);


    }

    @Override
    public Deployment imageUpdate(String namespace, String deployment, String image) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.hasText(image, () -> "image name is null");

        Log.debug(this, null, String.format("'%s' Namespaced Deployment '%s' image patched '%s'", namespace, deployment, image));
        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling()
                .updateImage(image);

    }
}

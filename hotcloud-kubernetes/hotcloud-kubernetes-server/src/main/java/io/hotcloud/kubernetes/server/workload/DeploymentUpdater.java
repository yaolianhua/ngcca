package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.TimeoutImageEditReplacePatchable;
import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.api.workload.DeploymentUpdateApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentUpdater implements DeploymentUpdateApi {

    private final KubernetesClient fabric8Client;

    public DeploymentUpdater(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public void scale(String namespace,
                      String deployment,
                      Integer count,
                      boolean wait) {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), () -> "deployment name is null");
        Assert.argument(Objects.nonNull(count), () -> "scale count is null");

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
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), () -> "deployment name is null");

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
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), () -> "deployment name is null");
        Assert.argument(!CollectionUtils.isEmpty(containerImage), () -> "containerImage map is empty");

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
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), () -> "deployment name is null");
        Assert.argument(StringUtils.hasText(image), () -> "image name is null");

        log.debug("Namespaced '{}' Deployment '{}' image patched '{}'", namespace, deployment, image);
        return fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rolling()
                .updateImage(image);

    }
}

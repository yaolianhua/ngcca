package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.workload.DeploymentUpdateApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

        log.info("Deployment '{}' scaled to num of '{}'", deployment, count);
        if (wait) {
            log.info("wait for the number of instances to exist - no guarantee is made as to readiness ");
        }

    }
}

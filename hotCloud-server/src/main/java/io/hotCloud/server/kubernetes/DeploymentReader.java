package io.hotCloud.server.kubernetes;

import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotCloud.core.kubernetes.deploy.V1DeploymentRead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentReader implements V1DeploymentRead {

    private final KubernetesClient fabric8Client;

    public DeploymentReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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

        DeploymentList deploymentList = fabric8Client.apps()
                .deployments()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
        return deploymentList;
    }
}

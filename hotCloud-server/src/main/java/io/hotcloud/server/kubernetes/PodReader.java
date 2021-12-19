package io.hotcloud.server.kubernetes;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.pod.PodReadApi;
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
public class PodReader implements PodReadApi {

    private final KubernetesClient fabric8Client;

    public PodReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PodList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.pods()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        PodList podList = fabric8Client.pods()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return podList;
    }
}

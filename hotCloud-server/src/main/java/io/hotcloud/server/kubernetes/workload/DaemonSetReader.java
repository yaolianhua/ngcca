package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.workload.DaemonSetReadApi;
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
public class DaemonSetReader implements DaemonSetReadApi {

    private final KubernetesClient fabric8Client;

    public DaemonSetReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public DaemonSetList read(String namespace, Map<String, String> labelSelector) {

        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.apps()
                    .daemonSets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        DaemonSetList daemonSetList = fabric8Client.apps()
                .daemonSets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
        return daemonSetList;
    }
}

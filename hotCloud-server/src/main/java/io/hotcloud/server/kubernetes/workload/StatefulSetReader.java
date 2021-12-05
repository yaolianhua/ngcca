package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.workload.StatefulSetReadApi;
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
public class StatefulSetReader implements StatefulSetReadApi {

    private final KubernetesClient fabric8Client;

    public StatefulSetReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public StatefulSetList read(String namespace, Map<String, String> labelSelector) {

        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.apps()
                    .statefulSets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        StatefulSetList statefulSetList = fabric8Client.apps()
                .statefulSets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
        return statefulSetList;
    }
}

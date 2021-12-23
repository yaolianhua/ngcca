package io.hotcloud.server.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.configmap.ConfigMapReadApi;
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
public class ConfigMapReader implements ConfigMapReadApi {

    private final KubernetesClient fabric8Client;

    public ConfigMapReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public ConfigMapList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.configMaps()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        ConfigMapList configMapList = fabric8Client.configMaps()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return configMapList;
    }
}

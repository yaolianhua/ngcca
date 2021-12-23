package io.hotcloud.core.kubernetes.configmap;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface ConfigMapReadApi {

    default ConfigMap read(String namespace, String configMap) {
        ConfigMapList configMapList = this.read(namespace);
        return configMapList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), configMap))
                .findFirst()
                .orElse(null);
    }

    default ConfigMapList read() {
        return this.read(null);
    }

    default ConfigMapList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    ConfigMapList read(String namespace, Map<String, String> labelSelector);
}

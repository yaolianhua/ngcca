package io.hotcloud.kubernetes.api.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ConfigMapApi {

    default ConfigMap configMap(ConfigMapCreateRequest request) throws ApiException {
        V1ConfigMap v1ConfigMap = ConfigMapBuilder.build(request);
        String json = Yaml.dump(v1ConfigMap);
        return this.configMap(json);
    }

    ConfigMap configMap(String yaml) throws ApiException;

    void delete(String namespace, String configmap) throws ApiException;

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

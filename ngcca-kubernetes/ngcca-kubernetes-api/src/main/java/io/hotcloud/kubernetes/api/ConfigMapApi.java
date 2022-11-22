package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.configurations.ConfigMapBuilder;
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

    /**
     * Create ConfigMap from {@code ConfigMapCreateRequest}
     *
     * @param request {@link ConfigMapCreateRequest}
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default ConfigMap create(ConfigMapCreateRequest request) throws ApiException {
        V1ConfigMap v1ConfigMap = ConfigMapBuilder.build(request);
        String json = Yaml.dump(v1ConfigMap);
        return this.create(json);
    }

    /**
     * Create ConfigMap from kubernetes yaml string
     *
     * @param yaml kubernetes yaml string
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    ConfigMap create(String yaml) throws ApiException;

    /**
     * Delete namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configmap configmap name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String configmap) throws ApiException;

    /**
     * Read namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configMap configmap name
     * @return {@link ConfigMap}
     */
    default ConfigMap read(String namespace, String configMap) {
        ConfigMapList configMapList = this.read(namespace);
        return configMapList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), configMap))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read ConfigMapList from all namespace
     *
     * @return {@link ConfigMapList}
     */
    default ConfigMapList read() {
        return this.read(null);
    }

    /**
     * Read namespaced ConfigMapList
     *
     * @param namespace namespace
     * @return {@link ConfigMapList}
     */
    default ConfigMapList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced ConfigMapList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ConfigMapList}
     */
    ConfigMapList read(String namespace, Map<String, String> labelSelector);
}

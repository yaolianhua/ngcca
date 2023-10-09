package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ConfigMapClient {

    /**
     * Read namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configmap configmap name
     * @return {@link ConfigMap}
     */
    ConfigMap read(String namespace, String configmap);

    /**
     * Read namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configmap configmap name
     * @return {@link ConfigMap}
     */
    ConfigMap read(String agentUrl, String namespace, String configmap);

    /**
     * Read namespaced ConfigMapList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ConfigMapList}
     */
    ConfigMapList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Read namespaced ConfigMapList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ConfigMapList}
     */
    ConfigMapList readList(String agentUrl, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced ConfigMapList
     *
     * @return {@link ConfigMapList}
     */
    ConfigMapList readList();

    /**
     * Read all namespaced ConfigMapList
     *
     * @return {@link ConfigMapList}
     */
    ConfigMapList readList(String agentUrl);

    /**
     * Create ConfigMap from {@code ConfigMapCreateRequest}
     *
     * @param request {@link ConfigMapCreateRequest}
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    ConfigMap create(ConfigMapCreateRequest request) throws ApiException;

    /**
     * Create ConfigMap from {@code ConfigMapCreateRequest}
     *
     * @param request {@link ConfigMapCreateRequest}
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    ConfigMap create(String agentUrl, ConfigMapCreateRequest request) throws ApiException;

    /**
     * Create ConfigMap from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    ConfigMap create(YamlBody yaml) throws ApiException;

    /**
     * Create ConfigMap from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link ConfigMap}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    ConfigMap create(String agentUrl, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configmap configmap name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String configmap) throws ApiException;

    /**
     * Delete namespaced ConfigMap
     *
     * @param namespace namespace
     * @param configmap configmap name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agentUrl, String namespace, String configmap) throws ApiException;

}

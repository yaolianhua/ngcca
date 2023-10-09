package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface SecretClient {

    /**
     * Read namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Secret}
     */
    default Secret read(String namespace, String secret) {
        return read(null, namespace, secret);
    }

    /**
     * Read namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Secret}
     */
    Secret read(String agent, String namespace, String secret);

    /**
     * Read namespaced SecretList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link SecretList}
     */
    default SecretList readList(String namespace, Map<String, String> labelSelector) {
        return readList(null, namespace, labelSelector);
    }

    /**
     * Read namespaced SecretList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link SecretList}
     */
    SecretList readList(String agent, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced SecretList
     *
     * @return {@link SecretList}
     */
    default SecretList readList() {
        return readList(null);
    }

    /**
     * Read all namespaced SecretList
     *
     * @return {@link SecretList}
     */
    SecretList readList(String agent);

    /**
     * Create Secret from {@code SecretCreateRequest}
     *
     * @param request {@link SecretCreateRequest}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Secret create(SecretCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create Secret from {@code SecretCreateRequest}
     *
     * @param request {@link SecretCreateRequest}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Secret create(String agent, SecretCreateRequest request) throws ApiException;

    /**
     * Create Secret from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Secret create(YamlBody yaml) throws ApiException {
        return create(null, yaml);
    }

    /**
     * Create Secret from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Secret create(String agent, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String namespace, String secret) throws ApiException {
        return delete(null, namespace, secret);
    }

    /**
     * Delete namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agent, String namespace, String secret) throws ApiException;

}

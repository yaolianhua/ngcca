package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface SecretHttpClient {

    /**
     * Read namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Secret}
     */
    Secret read(String namespace, String secret);

    /**
     * Read namespaced SecretList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link SecretList}
     */
    SecretList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create Secret from {@code SecretCreateRequest}
     *
     * @param request {@link SecretCreateRequest}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Secret create(SecretCreateRequest request) throws ApiException;

    /**
     * Create Secret from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Secret create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String secret) throws ApiException;

}

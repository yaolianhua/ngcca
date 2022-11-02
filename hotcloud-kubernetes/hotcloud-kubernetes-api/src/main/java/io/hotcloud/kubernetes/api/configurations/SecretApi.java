package io.hotcloud.kubernetes.api.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface SecretApi {
    /**
     * Create Secret from {@code SecretCreateRequest}
     *
     * @param request {@link SecretCreateRequest}
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Secret create(SecretCreateRequest request) throws ApiException {
        V1Secret v1Secret = SecretBuilder.build(request);
        String json = Yaml.dump(v1Secret);
        return this.create(json);
    }

    /**
     * Create Secret from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link Secret}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Secret create(String yaml) throws ApiException;

    /**
     * Delete namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String secret) throws ApiException;

    /**
     * Read namespaced Secret
     *
     * @param namespace namespace
     * @param secret    secret name
     * @return {@link Secret}
     */
    default Secret read(String namespace, String secret) {
        SecretList secretList = this.read(namespace);
        return secretList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), secret))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read SecretList all namespace
     *
     * @return {@link SecretList}
     */
    default SecretList read() {
        return this.read(null);
    }

    /**
     * Read namespaced SecretList
     *
     * @param namespace namespace
     * @return {@link Secret}
     */
    default SecretList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced SecretList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link SecretList}
     */
    SecretList read(String namespace, Map<String, String> labelSelector);
}

package io.hotcloud.kubernetes.api;

import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface NamespaceApi {

    /**
     * Create namespace from {@code NamespaceCreateRequest}
     *
     * @param namespaceCreateRequest {@link NamespaceCreateRequest}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void namespace(NamespaceCreateRequest namespaceCreateRequest) throws ApiException;

    /**
     * Create namespace
     *
     * @param namespace namespace name will be created
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default void namespace(String namespace) throws ApiException {
        NamespaceCreateRequest params = new NamespaceCreateRequest();
        ObjectMetadata namespaceMetadata = new ObjectMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.namespace(params);
    }

    void delete(String namespace) throws ApiException;
}

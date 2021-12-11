package io.hotcloud.core.kubernetes;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface NamespaceCreateApi {

    void namespace(NamespaceCreateParams params) throws ApiException;

    default void namespace(String namespace) throws ApiException {
        NamespaceCreateParams params = new NamespaceCreateParams();
        ObjectMetadata namespaceMetadata = new ObjectMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.namespace(params);
    }
}

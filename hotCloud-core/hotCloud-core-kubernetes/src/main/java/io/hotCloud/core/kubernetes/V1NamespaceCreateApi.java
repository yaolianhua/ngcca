package io.hotCloud.core.kubernetes;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1NamespaceCreateApi {

    void namespace(NamespaceCreateParams params) throws ApiException;

    default void namespace(String namespace) throws ApiException {
        NamespaceCreateParams params = new NamespaceCreateParams();
        NamespaceMetadata namespaceMetadata = new NamespaceMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.namespace(params);
    }
}

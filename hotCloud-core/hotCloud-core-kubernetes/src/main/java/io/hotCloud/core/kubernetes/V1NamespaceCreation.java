package io.hotCloud.core.kubernetes;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1NamespaceCreation {

    void namespace(NamespaceCreationParams params) throws ApiException;

    default void namespace(String namespace) throws ApiException {
        NamespaceCreationParams params = new NamespaceCreationParams();
        NamespaceMetadata namespaceMetadata = new NamespaceMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.namespace(params);
    }
}

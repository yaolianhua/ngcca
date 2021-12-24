package io.hotcloud.kubernetes.api;

import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface NamespaceCreateApi {

    void namespace(NamespaceCreateRequest params) throws ApiException;

    default void namespace(String namespace) throws ApiException {
        NamespaceCreateRequest params = new NamespaceCreateRequest();
        ObjectMetadata namespaceMetadata = new ObjectMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.namespace(params);
    }
}

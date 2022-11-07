package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.kubernetes.client.openapi.ApiException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
    void create(NamespaceCreateRequest namespaceCreateRequest) throws ApiException;

    /**
     * Create namespace
     *
     * @param namespace namespace name will be created
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default void create(String namespace) throws ApiException {
        NamespaceCreateRequest params = new NamespaceCreateRequest();
        ObjectMetadata namespaceMetadata = new ObjectMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        this.create(params);
    }

    /**
     * Delete namespace
     *
     * @param namespace namespace name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace) throws ApiException;

    /**
     * Read named Namespace
     *
     * @param name Namespace name
     * @return {@link Namespace}
     */
    default Namespace read(String name) {
        NamespaceList namespaceList = this.read(Collections.emptyMap());
        return namespaceList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read NamespaceList
     *
     * @param labelSelector label selector
     * @return {@link NamespaceList}
     */
    NamespaceList read(Map<String, String> labelSelector);
}

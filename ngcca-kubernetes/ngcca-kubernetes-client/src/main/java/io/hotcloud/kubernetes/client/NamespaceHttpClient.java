package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface NamespaceHttpClient {

    /**
     * Create namespace from {@code NamespaceCreateRequest}
     *
     * @param namespaceCreateRequest {@link NamespaceCreateRequest}
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void create(NamespaceCreateRequest namespaceCreateRequest) throws ApiException;

    /**
     * Create namespace
     *
     * @param namespace namespace name will be created
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void create(String namespace) throws ApiException {
        Assert.hasText(namespace, "namespace is null");
        NamespaceCreateRequest params = new NamespaceCreateRequest();
        ObjectMetadata namespaceMetadata = new ObjectMetadata();
        namespaceMetadata.setName(namespace);
        params.setMetadata(namespaceMetadata);
        return this.create(params);
    }

    /**
     * Delete namespace
     *
     * @param namespace namespace name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace) throws ApiException;

    /**
     * Read named Namespace
     *
     * @param name Namespace name
     * @return {@link Namespace}
     */
    Namespace read(String name);

    /**
     * Read NamespaceList
     *
     * @param labelSelector label selector
     * @return {@link NamespaceList}
     */
    NamespaceList readList(Map<String, String> labelSelector);
}

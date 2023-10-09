package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface StorageClassClient {

    /**
     * Create StorageClass from {@code StorageClassCreateRequest}
     *
     * @param request {@link StorageClassCreateRequest}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StorageClass create(StorageClassCreateRequest request) throws ApiException;

    /**
     * Create StorageClass from {@code StorageClassCreateRequest}
     *
     * @param request {@link StorageClassCreateRequest}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StorageClass create(String agent, StorageClassCreateRequest request) throws ApiException;

    /**
     * Create StorageClass from yaml
     *
     * @param yaml {@link YamlBody}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StorageClass create(YamlBody yaml) throws ApiException;

    /**
     * Create StorageClass from yaml
     *
     * @param yaml {@link YamlBody}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StorageClass create(String agent, YamlBody yaml) throws ApiException;

    /**
     * Delete named StorageClass
     *
     * @param storageClass StorageClass name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String storageClass) throws ApiException;

    /**
     * Delete named StorageClass
     *
     * @param storageClass StorageClass name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agent, String storageClass) throws ApiException;

    /**
     * Read named StorageClass
     *
     * @param name StorageClass name
     * @return {@link StorageClass}
     */
    StorageClass read(String name);

    /**
     * Read named StorageClass
     *
     * @param name StorageClass name
     * @return {@link StorageClass}
     */
    StorageClass read(String agent, String name);

    /**
     * Read StorageClassList
     *
     * @param labelSelector label selector
     * @return {@link StorageClassList}
     */
    StorageClassList readList(Map<String, String> labelSelector);

    /**
     * Read StorageClassList
     *
     * @param labelSelector label selector
     * @return {@link StorageClassList}
     */
    StorageClassList readList(String agent, Map<String, String> labelSelector);
}

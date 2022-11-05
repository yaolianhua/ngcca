package io.hotcloud.kubernetes.client.storage;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.model.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface StorageClassHttpClient {

    /**
     * Create StorageClass from {@code StorageClassCreateRequest}
     *
     * @param request {@link StorageClassCreateRequest}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<StorageClass> create(StorageClassCreateRequest request) throws ApiException;

    /**
     * Create StorageClass from yaml
     *
     * @param yaml {@link YamlBody}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<StorageClass> create(YamlBody yaml) throws ApiException;

    /**
     * Delete named StorageClass
     *
     * @param storageClass StorageClass name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String storageClass) throws ApiException;

    /**
     * Read named StorageClass
     *
     * @param name StorageClass name
     * @return {@link StorageClass}
     */
    Result<StorageClass> read(String name);

    /**
     * Read StorageClassList
     *
     * @param labelSelector label selector
     * @return {@link StorageClassList}
     */
    Result<StorageClassList> readList(Map<String, String> labelSelector);
}

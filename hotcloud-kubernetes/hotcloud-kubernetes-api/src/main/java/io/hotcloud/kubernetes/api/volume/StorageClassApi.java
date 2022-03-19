package io.hotcloud.kubernetes.api.volume;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.model.volume.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1StorageClass;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface StorageClassApi {
    /**
     * Create StorageClass from {@code StorageClassCreateRequest}
     *
     * @param request {@link StorageClassCreateRequest}
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default StorageClass storageClass(StorageClassCreateRequest request) throws ApiException {
        V1StorageClass v1StorageClass = StorageClassBuilder.build(request);
        String json = Yaml.dump(v1StorageClass);
        return this.storageClass(json);
    }

    /**
     * Create StorageClass from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link StorageClass}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StorageClass storageClass(String yaml) throws ApiException;

    /**
     * Delete named StorageClass
     *
     * @param storageClass StorageClass name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String storageClass) throws ApiException;

    /**
     * Read named StorageClass
     *
     * @param name StorageClass name
     * @return {@link StorageClass}
     */
    default StorageClass read(String name) {
        StorageClassList storageClassList = this.read(Collections.emptyMap());
        return storageClassList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read StorageClassList
     *
     * @param labelSelector label selector
     * @return {@link StorageClassList}
     */
    StorageClassList read(Map<String, String> labelSelector);
}

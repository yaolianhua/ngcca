package io.hotcloud.kubernetes.client.storage;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeClaimHttpClient {

    /**
     * Read namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link PersistentVolumeClaim}
     */
    Result<PersistentVolumeClaim> read(String namespace, String persistentVolumeClaim);

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PersistentVolumeClaimList}
     */
    Result<PersistentVolumeClaimList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create PersistentVolumeClaim from {@code PersistentVolumeClaimCreateRequest}
     *
     * @param request {@link PersistentVolumeClaimCreateRequest}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolumeClaim> create(PersistentVolumeClaimCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolumeClaim from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolumeClaim> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String persistentVolumeClaim) throws ApiException;

}

package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
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
     * @return {@link Result}
     */
    Result<PersistentVolumeClaim> read(String namespace, String persistentVolumeClaim);

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link Result}
     */
    Result<PersistentVolumeClaimList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create PersistentVolumeClaim from {@code PersistentVolumeClaimCreateRequest}
     *
     * @param request {@link PersistentVolumeClaimCreateRequest}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolumeClaim> create(PersistentVolumeClaimCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolumeClaim from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolumeClaim> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String persistentVolumeClaim) throws ApiException;

}

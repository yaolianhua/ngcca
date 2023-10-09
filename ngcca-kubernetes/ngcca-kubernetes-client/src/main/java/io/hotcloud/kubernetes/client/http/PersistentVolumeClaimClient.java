package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeClaimClient {

    /**
     * Read namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link PersistentVolumeClaim}
     */
    PersistentVolumeClaim read(String namespace, String persistentVolumeClaim);

    /**
     * Read namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link PersistentVolumeClaim}
     */
    PersistentVolumeClaim read(String agent, String namespace, String persistentVolumeClaim);

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PersistentVolumeClaimList}
     */
    PersistentVolumeClaimList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PersistentVolumeClaimList}
     */
    PersistentVolumeClaimList readList(String agent, String namespace, Map<String, String> labelSelector);

    /**
     * Create PersistentVolumeClaim from {@code PersistentVolumeClaimCreateRequest}
     *
     * @param request {@link PersistentVolumeClaimCreateRequest}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolumeClaim create(PersistentVolumeClaimCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolumeClaim from {@code PersistentVolumeClaimCreateRequest}
     *
     * @param request {@link PersistentVolumeClaimCreateRequest}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolumeClaim create(String agent, PersistentVolumeClaimCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolumeClaim from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolumeClaim create(YamlBody yaml) throws ApiException;

    /**
     * Create PersistentVolumeClaim from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolumeClaim create(String agent, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String persistentVolumeClaim) throws ApiException;

    /**
     * Delete namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agent, String namespace, String persistentVolumeClaim) throws ApiException;

}

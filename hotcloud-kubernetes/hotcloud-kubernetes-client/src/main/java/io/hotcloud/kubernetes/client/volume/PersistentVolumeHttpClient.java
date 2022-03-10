package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeHttpClient {

    /**
     * Read namespaced PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link PersistentVolume}
     */
    Result<PersistentVolume> read(String persistentVolume);

    /**
     * Read namespaced PersistentVolumeList
     *
     * @param labelSelector label selector
     * @return {@link PersistentVolumeList}
     */
    Result<PersistentVolumeList> readList(Map<String, String> labelSelector);

    /**
     * Create PersistentVolume from {@code PersistentVolumeCreateRequest}
     *
     * @param request {@link PersistentVolumeCreateRequest}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolume> create(PersistentVolumeCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolume from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<PersistentVolume> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String persistentVolume) throws ApiException;

}

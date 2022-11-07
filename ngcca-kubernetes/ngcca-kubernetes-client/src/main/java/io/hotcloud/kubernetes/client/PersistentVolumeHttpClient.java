package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeHttpClient {

    /**
     * Read named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link PersistentVolume}
     */
    PersistentVolume read(String persistentVolume);

    /**
     * Read PersistentVolumeList
     *
     * @param labelSelector label selector
     * @return {@link PersistentVolumeList}
     */
    PersistentVolumeList readList(Map<String, String> labelSelector);

    /**
     * Create PersistentVolume from {@code PersistentVolumeCreateRequest}
     *
     * @param request {@link PersistentVolumeCreateRequest}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolume create(PersistentVolumeCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolume from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolume create(YamlBody yaml) throws ApiException;

    /**
     * Delete named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String persistentVolume) throws ApiException;

}

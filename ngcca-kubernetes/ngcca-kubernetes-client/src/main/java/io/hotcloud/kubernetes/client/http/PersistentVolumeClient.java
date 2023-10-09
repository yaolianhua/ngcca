package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeClient {

    /**
     * Read named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link PersistentVolume}
     */
    default PersistentVolume read(String persistentVolume) {
        return read(null, persistentVolume);
    }

    /**
     * Read named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link PersistentVolume}
     */
    PersistentVolume read(String agent, String persistentVolume);

    /**
     * Read PersistentVolumeList
     *
     * @param labelSelector label selector
     * @return {@link PersistentVolumeList}
     */
    default PersistentVolumeList readList(Map<String, String> labelSelector) {
        return readList(null, labelSelector);
    }

    /**
     * Read PersistentVolumeList
     *
     * @param labelSelector label selector
     * @return {@link PersistentVolumeList}
     */
    PersistentVolumeList readList(String agent, Map<String, String> labelSelector);

    /**
     * Create PersistentVolume from {@code PersistentVolumeCreateRequest}
     *
     * @param request {@link PersistentVolumeCreateRequest}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default PersistentVolume create(PersistentVolumeCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create PersistentVolume from {@code PersistentVolumeCreateRequest}
     *
     * @param request {@link PersistentVolumeCreateRequest}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolume create(String agent, PersistentVolumeCreateRequest request) throws ApiException;

    /**
     * Create PersistentVolume from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default PersistentVolume create(YamlBody yaml) throws ApiException {
        return create(null, yaml);
    }

    /**
     * Create PersistentVolume from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolume create(String agent, YamlBody yaml) throws ApiException;

    /**
     * Delete named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String persistentVolume) throws ApiException {
        return delete(null, persistentVolume);
    }

    /**
     * Delete named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agent, String persistentVolume) throws ApiException;

}

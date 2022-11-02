package io.hotcloud.kubernetes.api.storage;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeApi {
    /**
     * Create PersistentVolume from {@code PersistentVolumeCreateRequest}
     *
     * @param request {@link PersistentVolumeCreateRequest}
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default PersistentVolume create(PersistentVolumeCreateRequest request) throws ApiException {
        V1PersistentVolume v1PersistentVolume = PersistentVolumeBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolume);
        return this.create(json);
    }

    /**
     * Create PersistentVolume from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link PersistentVolume}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolume create(String yaml) throws ApiException;

    /**
     * Delete named PersistentVolume
     *
     * @param persistentVolume persistentVolume name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String persistentVolume) throws ApiException;

    /**
     * Read named PersistentVolume
     *
     * @param name persistentVolume name
     * @return {@link PersistentVolume}
     */
    default PersistentVolume read(String name) {
        PersistentVolumeList persistentVolumeList = this.read(Collections.emptyMap());
        return persistentVolumeList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read PersistentVolumeList
     *
     * @param labelSelector label selector
     * @return {@link PersistentVolumeList}
     */
    PersistentVolumeList read(Map<String, String> labelSelector);
}

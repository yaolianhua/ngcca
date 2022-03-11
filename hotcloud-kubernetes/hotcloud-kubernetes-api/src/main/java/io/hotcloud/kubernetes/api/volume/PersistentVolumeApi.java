package io.hotcloud.kubernetes.api.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeCreateRequest;
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

    default PersistentVolume persistentVolume(PersistentVolumeCreateRequest request) throws ApiException {
        V1PersistentVolume v1PersistentVolume = PersistentVolumeBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolume);
        return this.persistentVolume(json);
    }

    PersistentVolume persistentVolume(String yaml) throws ApiException;

    void delete(String persistentVolume) throws ApiException;

    default PersistentVolume read(String name) {
        PersistentVolumeList persistentVolumeList = this.read(Collections.emptyMap());
        return persistentVolumeList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    PersistentVolumeList read(Map<String, String> labelSelector);
}

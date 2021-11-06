package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeCreateApi {

    default V1PersistentVolume persistentVolume(PersistentVolumeCreateParams request) throws ApiException {
        V1PersistentVolume v1PersistentVolume = PersistentVolumeBuilder.buildV1PersistentVolume(request);
        String json = Yaml.dump(v1PersistentVolume);
        return this.persistentVolume(json);
    }

    V1PersistentVolume persistentVolume(String yaml) throws ApiException;
}

package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1PersistentVolumeCreation {

    default V1PersistentVolume persistentVolume(PersistentVolumeCreationParam request) throws ApiException {
        V1PersistentVolume v1Service = V1PersistentVolumeBuilder.buildV1PersistentVolume(request);
        String json = Yaml.dump(v1Service);
        return this.persistentVolume(json);
    }

    V1PersistentVolume persistentVolume(String yaml) throws ApiException;
}

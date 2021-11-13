package io.hotcloud.core.kubernetes.volumes;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeCreateApi {

    default PersistentVolume persistentVolume(PersistentVolumeCreateParams request) throws ApiException {
        V1PersistentVolume v1PersistentVolume = PersistentVolumeBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolume);
        return this.persistentVolume(json);
    }

    PersistentVolume persistentVolume(String yaml) throws ApiException;
}

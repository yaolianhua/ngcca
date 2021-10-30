package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1PersistentVolumeClaimCreateApi {

    default V1PersistentVolumeClaim persistentVolumeClaim(PersistentVolumeClaimCreateParams request) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = V1PersistentVolumeClaimBuilder.buildV1PersistentVolumeClaim(request);
        String json = Yaml.dump(v1PersistentVolumeClaim);
        return this.persistentVolumeClaim(json);
    }

    V1PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException;
}

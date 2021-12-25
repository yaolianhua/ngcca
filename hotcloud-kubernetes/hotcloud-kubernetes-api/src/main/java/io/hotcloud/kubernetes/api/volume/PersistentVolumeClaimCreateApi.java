package io.hotcloud.kubernetes.api.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeClaimCreateApi {

    default PersistentVolumeClaim persistentVolumeClaim(PersistentVolumeClaimCreateRequest request) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = PersistentVolumeClaimBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolumeClaim);
        return this.persistentVolumeClaim(json);
    }

    PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException;
}

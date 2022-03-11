package io.hotcloud.kubernetes.api.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeClaimApi {

    default PersistentVolumeClaim persistentVolumeClaim(PersistentVolumeClaimCreateRequest request) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = PersistentVolumeClaimBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolumeClaim);
        return this.persistentVolumeClaim(json);
    }

    PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException;

    void delete(String persistentVolumeClaim, String namespace) throws ApiException;

    default PersistentVolumeClaim read(String namespace, String name) {
        PersistentVolumeClaimList persistentVolumeClaimList = this.read(namespace);
        return persistentVolumeClaimList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    default PersistentVolumeClaimList read() {
        return this.read(null);
    }

    default PersistentVolumeClaimList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    PersistentVolumeClaimList read(String namespace, Map<String, String> labelSelector);
}

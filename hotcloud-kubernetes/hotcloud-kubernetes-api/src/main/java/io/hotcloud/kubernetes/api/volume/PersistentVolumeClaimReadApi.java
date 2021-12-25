package io.hotcloud.kubernetes.api.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeClaimReadApi {

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

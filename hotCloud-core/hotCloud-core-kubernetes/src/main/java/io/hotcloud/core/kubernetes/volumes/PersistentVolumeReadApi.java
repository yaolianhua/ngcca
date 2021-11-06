package io.hotcloud.core.kubernetes.volumes;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeReadApi {

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

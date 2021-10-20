package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.models.V1PersistentVolume;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1PersistentVolumeBuilder {
    public static final String KIND = "PersistentVolume";
    public static final String VERSION = "v1";

    private V1PersistentVolumeBuilder() {
    }

    public static V1PersistentVolume buildV1PersistentVolume(PersistentVolumeCreationParam param) {

        V1PersistentVolume v1PersistentVolume = new V1PersistentVolume();


        return v1PersistentVolume;
    }
}

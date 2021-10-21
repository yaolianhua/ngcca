package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.HotCloudException;
import io.hotCloud.core.kubernetes.volumes.V1PersistentVolumeCreation;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeCreator implements V1PersistentVolumeCreation {

    private final CoreV1Api coreV1Api;

    public PersistentVolumeCreator(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public V1PersistentVolume persistentVolume(String yaml) throws ApiException {
        V1PersistentVolume v1PersistentVolume;
        try {
            v1PersistentVolume = (V1PersistentVolume) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load persistentVolume yaml error. '%s'", e.getMessage()));
        }

        V1PersistentVolume pv = coreV1Api.createPersistentVolume(v1PersistentVolume,
                "true",
                null,
                null);
        return pv;
    }
}

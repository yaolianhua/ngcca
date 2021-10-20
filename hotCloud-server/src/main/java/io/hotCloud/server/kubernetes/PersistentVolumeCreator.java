package io.hotCloud.server.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotCloud.core.kubernetes.volumes.V1PersistentVolumeCreation;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeCreator implements V1PersistentVolumeCreation {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public PersistentVolumeCreator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public V1PersistentVolume persistentVolume(String yaml) throws ApiException {
        return null;
    }
}

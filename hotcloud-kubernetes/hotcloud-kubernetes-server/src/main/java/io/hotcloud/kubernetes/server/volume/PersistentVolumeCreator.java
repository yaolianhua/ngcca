package io.hotcloud.kubernetes.server.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeCreator implements PersistentVolumeCreateApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public PersistentVolumeCreator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PersistentVolume persistentVolume(String yaml) throws ApiException {
        V1PersistentVolume v1PersistentVolume;
        try {
            v1PersistentVolume = Yaml.loadAs(yaml, V1PersistentVolume.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load persistentVolume yaml error. '%s'", e.getMessage()));
        }

        V1PersistentVolume v1Pv = coreV1Api.createPersistentVolume(
                v1PersistentVolume,
                "true",
                null,
                null);
        log.debug("create persistentVolume success \n '{}'", v1Pv);
        PersistentVolume pv = fabric8Client.persistentVolumes()
                .withName(Objects.requireNonNull(v1PersistentVolume.getMetadata(), "get v1PersistentVolume metadata null").getName())
                .get();

        return pv;
    }
}

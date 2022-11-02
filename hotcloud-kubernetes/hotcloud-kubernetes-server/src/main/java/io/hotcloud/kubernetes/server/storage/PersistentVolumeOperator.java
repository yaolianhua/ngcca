package io.hotcloud.kubernetes.server.storage;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeOperator implements PersistentVolumeApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public PersistentVolumeOperator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PersistentVolume create(String yaml) throws ApiException {
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
                null, null);
        log.debug("create persistentVolume success \n '{}'", v1Pv);

        return fabric8Client.persistentVolumes()
                .withName(Objects.requireNonNull(v1PersistentVolume.getMetadata(), "get v1PersistentVolume metadata null").getName())
                .get();
    }

    @Override
    public PersistentVolumeList read(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;

        return fabric8Client
                .persistentVolumes()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void delete(String persistentVolume) throws ApiException {
        Assert.hasText(persistentVolume, () -> "delete resource name is null");
        V1PersistentVolume v1PersistentVolume = coreV1Api.deletePersistentVolume(
                persistentVolume,
                "true",
                null,
                null,
                null,
                null,
                null);
        log.debug("Delete persistentVolume success \n '{}'", v1PersistentVolume);
    }
}

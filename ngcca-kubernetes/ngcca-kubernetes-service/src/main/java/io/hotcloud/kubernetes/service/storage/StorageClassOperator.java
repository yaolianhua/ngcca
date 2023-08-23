package io.hotcloud.kubernetes.service.storage;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.StorageClassApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.StorageV1Api;
import io.kubernetes.client.openapi.models.V1StorageClass;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class StorageClassOperator implements StorageClassApi {

    private final StorageV1Api storageV1Api;
    private final KubernetesClient fabric8Client;

    public StorageClassOperator(StorageV1Api storageV1Api, KubernetesClient fabric8Client) {
        this.storageV1Api = storageV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public StorageClass create(String yaml) throws ApiException {
        V1StorageClass v1StorageClass;
        try {
            v1StorageClass = Yaml.loadAs(yaml, V1StorageClass.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load storageClass yaml error. '%s'", e.getMessage()));
        }

        V1StorageClass v1Sc = storageV1Api.createStorageClass(
                v1StorageClass,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create storageClass '%s' success", Objects.requireNonNull(v1Sc.getMetadata()).getName()));

        return fabric8Client.storage()
                .v1()
                .storageClasses()
                .withName(Objects.requireNonNull(v1StorageClass.getMetadata(), "get v1StorageClass metadata null").getName())
                .get();
    }

    @Override
    public StorageClassList read(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;

        return fabric8Client
                .storage()
                .v1()
                .storageClasses()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void delete(String storageClass) throws ApiException {
        Assert.hasText(storageClass, () -> "delete resource name is null");
        storageV1Api.deleteStorageClass(
                storageClass,
                "true",
                null,
                null,
                null,
                null,
                null);
        Log.debug(this, null, String.format("delete storageClass '%s' success", storageClass));
    }
}

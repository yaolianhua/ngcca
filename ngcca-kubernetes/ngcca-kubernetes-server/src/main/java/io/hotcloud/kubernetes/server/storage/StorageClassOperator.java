package io.hotcloud.kubernetes.server.storage;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.storage.StorageClassApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.StorageV1Api;
import io.kubernetes.client.openapi.models.V1StorageClass;
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
        log.debug("create storageClass success \n '{}'", v1Sc);

        return fabric8Client.storage()
                .storageClasses()
                .withName(Objects.requireNonNull(v1StorageClass.getMetadata(), "get v1StorageClass metadata null").getName())
                .get();
    }

    @Override
    public StorageClassList read(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;

        return fabric8Client
                .storage()
                .storageClasses()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void delete(String storageClass) throws ApiException {
        Assert.hasText(storageClass, () -> "delete resource name is null");
        V1StorageClass v1StorageClass = storageV1Api.deleteStorageClass(
                storageClass,
                "true",
                null,
                null,
                null,
                null,
                null);
        log.debug("Delete storageClass success \n '{}'", v1StorageClass);
    }
}

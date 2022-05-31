package io.hotcloud.kubernetes.server.storage;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static io.hotcloud.common.UUIDGenerator.DEFAULT;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeClaimOperator implements PersistentVolumeClaimApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public PersistentVolumeClaimOperator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim;
        try {
            v1PersistentVolumeClaim = Yaml.loadAs(yaml, V1PersistentVolumeClaim.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load persistentVolumeClaim yaml error. '%s'", e.getMessage()));
        }

        String namespace = Objects.requireNonNull(v1PersistentVolumeClaim.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT;
        V1PersistentVolumeClaim v1Pvc = coreV1Api.createNamespacedPersistentVolumeClaim(
                namespace,
                v1PersistentVolumeClaim,
                "true",
                null,
                null, null);
        log.debug("create persistentVolumeClaim success \n '{}'", v1Pvc);

        return fabric8Client.persistentVolumeClaims()
                .inNamespace(namespace)
                .withName(v1PersistentVolumeClaim.getMetadata().getName())
                .get();
    }

    @Override
    public PersistentVolumeClaimList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client
                    .persistentVolumeClaims()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client
                .persistentVolumeClaims()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void delete(String persistentVolumeClaim, String namespace) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(persistentVolumeClaim, () -> "delete resource name is null");
        V1PersistentVolumeClaim v1PersistentVolumeClaim = coreV1Api.deleteNamespacedPersistentVolumeClaim(
                persistentVolumeClaim,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null);
        log.debug("Delete namespaced persistentVolumeClaim success \n '{}'", v1PersistentVolumeClaim);
    }
}

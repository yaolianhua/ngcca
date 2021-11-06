package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.HotCloudException;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeClaimCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeClaimCreator implements PersistentVolumeClaimCreateApi {

    private final CoreV1Api coreV1Api;

    public PersistentVolumeClaimCreator(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public V1PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim;
        try {
            v1PersistentVolumeClaim = (V1PersistentVolumeClaim) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load persistentVolumeClaim yaml error. '%s'", e.getMessage()));
        }

        String namespace = Objects.requireNonNull(v1PersistentVolumeClaim.getMetadata(), "namespace is empty").getNamespace();
        V1PersistentVolumeClaim pvc = coreV1Api.createNamespacedPersistentVolumeClaim(
                namespace,
                v1PersistentVolumeClaim,
                "true",
                null,
                null);
        return pvc;
    }
}

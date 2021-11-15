package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeClaimCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

import static io.hotcloud.core.kubernetes.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeClaimCreator implements PersistentVolumeClaimCreateApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public PersistentVolumeClaimCreator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PersistentVolumeClaim persistentVolumeClaim(String yaml) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim;
        try {
            v1PersistentVolumeClaim = (V1PersistentVolumeClaim) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load persistentVolumeClaim yaml error. '%s'", e.getMessage()));
        }

        String namespace = Objects.requireNonNull(v1PersistentVolumeClaim.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1PersistentVolumeClaim v1Pvc = coreV1Api.createNamespacedPersistentVolumeClaim(
                namespace,
                v1PersistentVolumeClaim,
                "true",
                null,
                null);
        log.debug("create persistentVolumeClaim success \n '{}'", v1Pvc);

        PersistentVolumeClaim pvc = fabric8Client.persistentVolumeClaims()
                .inNamespace(namespace)
                .withName(v1PersistentVolumeClaim.getMetadata().getName())
                .get();
        return pvc;
    }
}

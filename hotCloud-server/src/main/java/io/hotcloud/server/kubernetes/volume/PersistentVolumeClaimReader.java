package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeClaimReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeClaimReader implements PersistentVolumeClaimReadApi {

    private final KubernetesClient fabric8Client;

    public PersistentVolumeClaimReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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

        PersistentVolumeClaimList persistentVolumeClaimList = fabric8Client
                .persistentVolumeClaims()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return persistentVolumeClaimList;
    }
}

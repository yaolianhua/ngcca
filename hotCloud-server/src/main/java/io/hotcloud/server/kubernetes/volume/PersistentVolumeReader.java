package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeReader implements PersistentVolumeReadApi {

    private final KubernetesClient fabric8Client;

    public PersistentVolumeReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public PersistentVolumeList read(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;

        PersistentVolumeList persistentVolumeClaimList = fabric8Client
                .persistentVolumes()
                .withLabels(labelSelector)
                .list();

        return persistentVolumeClaimList;
    }
}

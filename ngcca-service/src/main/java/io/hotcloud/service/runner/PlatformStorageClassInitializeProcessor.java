package io.hotcloud.service.runner;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.StorageClassClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.hotcloud.service.volume.StorageClassName;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlatformStorageClassInitializeProcessor implements RunnerProcessor {

    private final StorageClassClient storageClassClient;
    private final KubernetesAgentProperties kubernetesAgentProperties;

    public PlatformStorageClassInitializeProcessor(StorageClassClient storageClassClient,
                                                   KubernetesAgentProperties kubernetesAgentProperties) {
        this.storageClassClient = storageClassClient;
        this.kubernetesAgentProperties = kubernetesAgentProperties;
    }

    @Override
    public void execute() {
        String agent = kubernetesAgentProperties.getDefaultEndpoint();
        try {
            StorageClass storageClass = storageClassClient.read(agent, StorageClassName.LOCAL_STORAGE);
            if (Objects.isNull(storageClass)) {
                StorageClassCreateRequest storageClassCreateRequest = new StorageClassCreateRequest();
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setName(StorageClassName.LOCAL_STORAGE);
                storageClassCreateRequest.setMetadata(objectMetadata);

                storageClassCreateRequest.setProvisioner("kubernetes.io/no-provisioner");
                storageClassCreateRequest.setVolumeBindingMode("WaitForFirstConsumer");
                storageClassClient.create(agent, storageClassCreateRequest);
                Log.info(this, agent, Event.START, "created storageClass " + StorageClassName.LOCAL_STORAGE);
            }
        } catch (Exception e) {
            throw new PlatformException("create storageClass error: " + e.getMessage());
        }
    }

}

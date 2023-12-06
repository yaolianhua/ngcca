package io.hotcloud.kubernetes.server.runner;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.api.StorageClassApi;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlatformStorageClassInitializeProcessor implements ApplicationRunner {

    private final StorageClassApi storageClassApi;

    public PlatformStorageClassInitializeProcessor(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            StorageClass storageClass = storageClassApi.read("local-storage");
            if (Objects.isNull(storageClass)) {
                StorageClassCreateRequest storageClassCreateRequest = new StorageClassCreateRequest();
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setName("local-storage");
                storageClassCreateRequest.setMetadata(objectMetadata);

                storageClassCreateRequest.setProvisioner("kubernetes.io/no-provisioner");
                storageClassCreateRequest.setVolumeBindingMode("WaitForFirstConsumer");
                storageClassApi.create(storageClassCreateRequest);
                Log.info(this, null, Event.START, "init StorageClass 'local-storage' success");
            }
        } catch (Exception e) {
            throw new PlatformException("init StorageClass error: " + e.getMessage());
        }
    }
}

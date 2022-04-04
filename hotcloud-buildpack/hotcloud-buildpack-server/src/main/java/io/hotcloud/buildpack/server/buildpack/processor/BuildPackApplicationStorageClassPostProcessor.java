package io.hotcloud.buildpack.server.buildpack.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.kubernetes.api.storage.StorageClassApi;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class BuildPackApplicationStorageClassPostProcessor implements BuildPackApplicationRunnerPostProcessor {

    private final StorageClassApi storageClassApi;
    private final BuildPackStorageProperties properties;

    public BuildPackApplicationStorageClassPostProcessor(StorageClassApi storageClassApi, BuildPackStorageProperties properties) {
        this.storageClassApi = storageClassApi;
        this.properties = properties;
    }

    @Override
    public void execute() {
        String storageClassName = properties.getStorageClass().getName();
        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(storageClassName);

        createRequest.setMetadata(objectMetadata);

        try {
            StorageClass existedStorageClass = storageClassApi.read(storageClassName);
            if (Objects.nonNull(existedStorageClass)) {
                log.info("BuildPack storageClass post processor. storageClass '{}' already exist ", storageClassName);
                return;
            }
            StorageClass storageClass = storageClassApi.storageClass(createRequest);
            log.info("BuildPack storageClass post processor. storageClass '{}' created ", storageClass.getMetadata().getName());
        } catch (ApiException e) {
            log.error("BuildPack storageClass post processor error: {}", e.getMessage());
        }
    }
}

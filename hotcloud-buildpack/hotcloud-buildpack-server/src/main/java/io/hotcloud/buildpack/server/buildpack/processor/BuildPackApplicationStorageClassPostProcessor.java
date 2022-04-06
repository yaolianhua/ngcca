package io.hotcloud.buildpack.server.buildpack.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.buildpack.api.BuildPackConstant;
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

        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(BuildPackConstant.STORAGE_CLASS);

        createRequest.setMetadata(objectMetadata);

        try {
            StorageClass existedStorageClass = storageClassApi.read(BuildPackConstant.STORAGE_CLASS);
            if (Objects.nonNull(existedStorageClass)) {
                log.info("BuildPackApplicationStorageClassPostProcessor. storageClass '{}' already exist ", BuildPackConstant.STORAGE_CLASS);
                return;
            }
            StorageClass storageClass = storageClassApi.storageClass(createRequest);
            log.info("BuildPackApplicationStorageClassPostProcessor. storageClass '{}' created ", storageClass.getMetadata().getName());
        } catch (ApiException e) {
            log.error("BuildPackApplicationStorageClassPostProcessor error: {}", e.getMessage());
        }
    }
}

package io.hotcloud.buildpack.server.core.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackPostProcessor;
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
class BuildPackStorageClassPostProcessor implements BuildPackPostProcessor {

    private final StorageClassApi storageClassApi;

    public BuildPackStorageClassPostProcessor(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
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
                log.debug("BuildPackStorageClassPostProcessor. storageClass '{}' already exist ", BuildPackConstant.STORAGE_CLASS);
                return;
            }
            StorageClass storageClass = storageClassApi.storageClass(createRequest);
            log.info("BuildPackStorageClassPostProcessor. storageClass '{}' created ", storageClass.getMetadata().getName());
        } catch (ApiException e) {
            log.error("BuildPackStorageClassPostProcessor error: {}", e.getMessage());
        }
    }
}

package io.hotcloud.application.server.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.storage.StorageClassApi;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
class ApplicationStorageClassRunnerProcessor implements ApplicationRunnerProcessor {

    private final StorageClassApi storageClassApi;

    public ApplicationStorageClassRunnerProcessor(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
    }

    @Override
    public void process() {

        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(ApplicationConstant.STORAGE_CLASS);

        createRequest.setMetadata(objectMetadata);

        try {
            StorageClass existedStorageClass = storageClassApi.read(ApplicationConstant.STORAGE_CLASS);
            if (Objects.nonNull(existedStorageClass)) {
                Log.debug(ApplicationStorageClassRunnerProcessor.class.getName(),
                        String.format("storageClass '%s' already exist ", ApplicationConstant.STORAGE_CLASS));
                return;
            }
            StorageClass storageClass = storageClassApi.storageClass(createRequest);
            Log.info(ApplicationStorageClassRunnerProcessor.class.getName(),
                    String.format("storageClass '%s' created ", storageClass.getMetadata().getName()));
        } catch (ApiException e) {
            Log.error(ApplicationStorageClassRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

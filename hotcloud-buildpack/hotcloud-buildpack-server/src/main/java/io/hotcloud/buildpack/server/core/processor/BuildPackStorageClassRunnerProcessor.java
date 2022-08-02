package io.hotcloud.buildpack.server.core.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.buildpack.api.BuildPackRunnerProcessor;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
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
@Deprecated(since = "BuildPackApiV2")
class BuildPackStorageClassRunnerProcessor implements BuildPackRunnerProcessor {

    private final StorageClassApi storageClassApi;

    public BuildPackStorageClassRunnerProcessor(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
    }

    @Override
    public void process() {

        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(BuildPackConstant.STORAGE_CLASS);

        createRequest.setMetadata(objectMetadata);

        try {
            StorageClass existedStorageClass = storageClassApi.read(BuildPackConstant.STORAGE_CLASS);
            if (Objects.nonNull(existedStorageClass)) {
                Log.debug(BuildPackStorageClassRunnerProcessor.class.getName(),
                        String.format("BuildPack storageClass '%s' already exist ", BuildPackConstant.STORAGE_CLASS));
                return;
            }
            StorageClass storageClass = storageClassApi.storageClass(createRequest);
            Log.info(BuildPackStorageClassRunnerProcessor.class.getName(),
                    String.format("Buildpack storageClass '%s' created ", storageClass.getMetadata().getName()));
        } catch (ApiException e) {
            Log.error(BuildPackStorageClassRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

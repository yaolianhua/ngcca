package io.hotcloud.server.buildpack.processor;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.StorageClassClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.hotcloud.server.NGCCARunnerProcessor;
import io.hotcloud.vendor.buildpack.BuildPackConstant;
import io.kubernetes.client.openapi.ApiException;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
//@Component
@Deprecated(since = "BuildPackApiV2")
class BuildPackStorageClassRunnerProcessor implements NGCCARunnerProcessor {

    private final StorageClassClient storageClassApi;

    public BuildPackStorageClassRunnerProcessor(StorageClassClient storageClassApi) {
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
                Log.debug(BuildPackStorageClassRunnerProcessor.class.getName(),
                        String.format("BuildPack storageClass '%s' already exist ", BuildPackConstant.STORAGE_CLASS));
                return;
            }
            StorageClass storageClass = storageClassApi.create(createRequest);
            Log.info(BuildPackStorageClassRunnerProcessor.class.getName(),
                    String.format("Buildpack storageClass '%s' created ", storageClass.getMetadata().getName()));
        } catch (ApiException e) {
            Log.error(BuildPackStorageClassRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

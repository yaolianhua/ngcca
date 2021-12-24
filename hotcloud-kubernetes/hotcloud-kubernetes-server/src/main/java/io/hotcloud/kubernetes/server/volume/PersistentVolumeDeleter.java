package io.hotcloud.kubernetes.server.volume;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeDeleter implements PersistentVolumeDeleteApi {

    private final CoreV1Api coreV1Api;

    public PersistentVolumeDeleter(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String persistentVolume) throws ApiException {
        Assert.argument(StringUtils.hasText(persistentVolume), () -> "delete resource name is null");
        V1PersistentVolume v1PersistentVolume = coreV1Api.deletePersistentVolume(
                persistentVolume,
                "true",
                null,
                null,
                null,
                null,
                null);
        log.debug("Delete persistentVolume success \n '{}'", v1PersistentVolume);
    }
}

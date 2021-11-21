package io.hotcloud.server.kubernetes.volume;

import io.hotcloud.core.kubernetes.volume.PersistentVolumeDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

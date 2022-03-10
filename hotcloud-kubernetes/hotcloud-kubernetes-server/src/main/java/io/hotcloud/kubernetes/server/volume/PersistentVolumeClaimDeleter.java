package io.hotcloud.kubernetes.server.volume;

import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PersistentVolumeClaimDeleter implements PersistentVolumeClaimDeleteApi {

    private final CoreV1Api coreV1Api;

    public PersistentVolumeClaimDeleter(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String persistentVolumeClaim, String namespace) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(persistentVolumeClaim), () -> "delete resource name is null");
        V1PersistentVolumeClaim v1PersistentVolumeClaim = coreV1Api.deleteNamespacedPersistentVolumeClaim(
                persistentVolumeClaim,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null);
        log.debug("Delete namespaced persistentVolumeClaim success \n '{}'", v1PersistentVolumeClaim);
    }
}

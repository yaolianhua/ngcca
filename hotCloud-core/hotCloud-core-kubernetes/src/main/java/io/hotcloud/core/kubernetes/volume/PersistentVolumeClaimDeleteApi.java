package io.hotcloud.core.kubernetes.volume;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeClaimDeleteApi {

    void delete(String persistentVolumeClaim, String namespace) throws ApiException;
}

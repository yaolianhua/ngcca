package io.hotcloud.kubernetes.api.volume;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeClaimDeleteApi {

    void delete(String persistentVolumeClaim, String namespace) throws ApiException;
}

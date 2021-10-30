package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1PersistentVolumeClaimDeleteApi {

    void delete(String persistentVolumeClaim, String namespace) throws ApiException;
}

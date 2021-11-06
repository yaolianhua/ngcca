package io.hotcloud.core.kubernetes.volumes;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PersistentVolumeDeleteApi {

    void delete(String persistentVolume) throws ApiException;
}

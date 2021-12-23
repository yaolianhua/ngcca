package io.hotcloud.core.kubernetes.service;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface ServiceDeleteApi {

    void delete(String namespace, String service) throws ApiException;

}

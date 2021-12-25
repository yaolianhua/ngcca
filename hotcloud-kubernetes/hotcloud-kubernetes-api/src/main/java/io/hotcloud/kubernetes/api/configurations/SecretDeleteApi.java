package io.hotcloud.kubernetes.api.configurations;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface SecretDeleteApi {

    void delete(String namespace, String secret) throws ApiException;

}

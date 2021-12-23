package io.hotcloud.core.kubernetes.secret;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface SecretDeleteApi {

    void delete(String namespace, String secret) throws ApiException;

}

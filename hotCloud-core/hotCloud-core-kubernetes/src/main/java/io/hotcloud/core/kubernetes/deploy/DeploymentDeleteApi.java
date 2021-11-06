package io.hotcloud.core.kubernetes.deploy;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DeploymentDeleteApi {

    void delete(String namespace, String deployment) throws ApiException;

}

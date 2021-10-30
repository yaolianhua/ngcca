package io.hotCloud.core.kubernetes.deploy;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1DeploymentDeleteApi {

    void delete(DeploymentDeleteParams params) throws ApiException;

}

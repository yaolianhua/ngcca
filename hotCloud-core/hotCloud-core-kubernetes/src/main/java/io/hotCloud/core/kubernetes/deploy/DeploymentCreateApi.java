package io.hotCloud.core.kubernetes.deploy;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DeploymentCreateApi {

    default V1Deployment deployment(DeploymentCreateParams request) throws ApiException {
        V1Deployment v1Deployment = DeploymentBuilder.build(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    V1Deployment deployment(String yaml) throws ApiException;

}

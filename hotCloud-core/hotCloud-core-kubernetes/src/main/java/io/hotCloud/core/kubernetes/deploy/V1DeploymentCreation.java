package io.hotCloud.core.kubernetes.deploy;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1DeploymentCreation {

    default V1Deployment deployment(DeploymentCreationParams request) throws ApiException {
        V1Deployment v1Deployment = V1DeploymentBuilder.buildV1Deployment(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    V1Deployment deployment(String yaml) throws ApiException;

}

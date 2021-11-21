package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DeploymentCreateApi {

    default Deployment deployment(DeploymentCreateParams request) throws ApiException {
        V1Deployment v1Deployment = DeploymentBuilder.build(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    Deployment deployment(String yaml) throws ApiException;

}

package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DeploymentCreateApi {

    default Deployment deployment(DeploymentCreateRequest request) throws ApiException {
        V1Deployment v1Deployment = DeploymentBuilder.build(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    Deployment deployment(String yaml) throws ApiException;

}

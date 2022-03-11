package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DeploymentApi {

    default Deployment deployment(DeploymentCreateRequest request) throws ApiException {
        V1Deployment v1Deployment = DeploymentBuilder.build(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    Deployment deployment(String yaml) throws ApiException;

    void delete(String namespace, String deployment) throws ApiException;

    default Deployment read(String namespace, String deployment) {
        DeploymentList deploymentList = this.read(namespace);
        return deploymentList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), deployment))
                .findFirst()
                .orElse(null);
    }

    default DeploymentList read() {
        return this.read(null);
    }

    default DeploymentList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    DeploymentList read(String namespace, Map<String, String> labelSelector);

    void scale(String namespace,
               String deployment,
               Integer count, boolean wait);

    Deployment rolling(RollingAction action,
                       String namespace,
                       String deployment);

    Deployment imageUpdate(Map<String, String> containerImage,
                           String namespace,
                           String deployment);

    Deployment imageUpdate(String namespace,
                           String deployment,
                           String image);
}

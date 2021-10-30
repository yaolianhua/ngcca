package io.hotCloud.core.kubernetes.deploy;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1DeploymentReadApi {

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

}

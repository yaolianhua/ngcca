package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public final class InstanceTemplateDeploymentStatus {

    private InstanceTemplateDeploymentStatus() {
    }

    public static boolean isReady(Deployment deployment) {
        Integer replicas = deployment.getStatus().getReplicas();
        Integer availableReplicas = deployment.getStatus().getAvailableReplicas();
        Integer readyReplicas = deployment.getStatus().getReadyReplicas();
        Integer updatedReplicas = deployment.getStatus().getUpdatedReplicas();

        return Objects.nonNull(replicas) &&
                Objects.nonNull(availableReplicas) &&
                Objects.nonNull(readyReplicas) &&
                Objects.nonNull(updatedReplicas) &&
                Objects.equals(readyReplicas, 1) &&
                Objects.equals(replicas, 1) &&
                Objects.equals(updatedReplicas, 1) &&
                Objects.equals(availableReplicas, 1);

    }

}

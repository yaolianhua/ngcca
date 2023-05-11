package io.hotcloud.server.module.application.template;

import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class TemplateInstanceDeploymentStatus {

    private TemplateInstanceDeploymentStatus() {
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

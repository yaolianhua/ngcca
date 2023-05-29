package io.hotcloud.service.module.application.core;

import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.Objects;

public final class ApplicationInstanceDeploymentStatus {

    private ApplicationInstanceDeploymentStatus() {
    }

    public static boolean isReady(Deployment deployment, int replica) {
        Integer replicas = deployment.getStatus().getReplicas();
        Integer availableReplicas = deployment.getStatus().getAvailableReplicas();
        Integer readyReplicas = deployment.getStatus().getReadyReplicas();
        Integer updatedReplicas = deployment.getStatus().getUpdatedReplicas();

        return Objects.nonNull(replicas) &&
                Objects.nonNull(availableReplicas) &&
                Objects.nonNull(readyReplicas) &&
                Objects.nonNull(updatedReplicas) &&
                Objects.equals(readyReplicas, replica) &&
                Objects.equals(replicas, replica) &&
                Objects.equals(updatedReplicas, replica) &&
                Objects.equals(availableReplicas, replica);

    }

}

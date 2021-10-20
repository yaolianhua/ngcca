package io.hotCloud.server.kubernetes;

import io.hotCloud.core.kubernetes.deploy.DeploymentDeletionParams;
import io.hotCloud.core.kubernetes.deploy.V1DeploymentDeletion;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentDeleter implements V1DeploymentDeletion {

    private final AppsV1Api appsV1Api;

    public DeploymentDeleter(AppsV1Api appsV1Api) {
        this.appsV1Api = appsV1Api;
    }

    @Override
    public void delete(DeploymentDeletionParams request) throws ApiException {
        V1Status v1Status = appsV1Api.deleteNamespacedDeployment(
                request.getName(),
                request.getNamespace(),
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced deployment success \n '{}'",v1Status);
    }
}

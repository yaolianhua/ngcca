package io.hotcloud.server.kubernetes.workload;

import io.hotcloud.core.kubernetes.workload.DeploymentDeleteApi;
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
public class DeploymentDeleter implements DeploymentDeleteApi {

    private final AppsV1Api appsV1Api;

    public DeploymentDeleter(AppsV1Api appsV1Api) {
        this.appsV1Api = appsV1Api;
    }

    @Override
    public void delete(String namespace, String deployment) throws ApiException {
        V1Status v1Status = appsV1Api.deleteNamespacedDeployment(
                deployment,
                namespace,
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

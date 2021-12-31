package io.hotcloud.kubernetes.server.workload;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.workload.DeploymentDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), () -> "delete resource name is null");
        V1Status v1Status = appsV1Api.deleteNamespacedDeployment(
                deployment,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        log.debug("delete namespaced deployment success \n '{}'",v1Status);
    }
}

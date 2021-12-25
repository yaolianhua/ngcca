package io.hotcloud.kubernetes.server.workload;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.workload.DaemonSetDeleteApi;
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
public class DaemonSetDeleter implements DaemonSetDeleteApi {

    private final AppsV1Api appsV1Api;

    public DaemonSetDeleter(AppsV1Api appsV1Api) {
        this.appsV1Api = appsV1Api;
    }

    @Override
    public void delete(String namespace, String daemonSet) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(daemonSet), () -> "delete resource name is null");
        V1Status v1Status = appsV1Api.deleteNamespacedDaemonSet(
                daemonSet,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced daemonSet success \n '{}'", v1Status);
    }
}

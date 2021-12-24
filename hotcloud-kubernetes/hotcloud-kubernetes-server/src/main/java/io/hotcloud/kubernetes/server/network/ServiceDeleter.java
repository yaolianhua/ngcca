package io.hotcloud.kubernetes.server.network;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.network.ServiceDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ServiceDeleter implements ServiceDeleteApi {

    private final CoreV1Api coreV1Api;

    public ServiceDeleter(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String namespace, String service) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(service), () -> "delete resource name is null");
        V1Status v1Status = coreV1Api.deleteNamespacedService(
                service,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced service success \n '{}'", v1Status);
    }
}

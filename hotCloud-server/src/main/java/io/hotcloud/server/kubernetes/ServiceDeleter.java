package io.hotcloud.server.kubernetes;

import io.hotcloud.core.kubernetes.service.ServiceDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

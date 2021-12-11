package io.hotcloud.server.kubernetes;

import io.hotcloud.core.kubernetes.configmap.ConfigMapDeleteApi;
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
public class ConfigMapDeleter implements ConfigMapDeleteApi {

    private final CoreV1Api coreV1Api;

    public ConfigMapDeleter(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String namespace, String configmap) throws ApiException {
        V1Status v1Status = coreV1Api.deleteNamespacedConfigMap(
                configmap,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced configMap success \n '{}'", v1Status);
    }
}

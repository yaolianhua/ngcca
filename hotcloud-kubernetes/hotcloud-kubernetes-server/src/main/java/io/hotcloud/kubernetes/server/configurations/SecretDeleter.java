package io.hotcloud.kubernetes.server.configurations;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.configurations.SecretDeleteApi;
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
public class SecretDeleter implements SecretDeleteApi {

    private final CoreV1Api coreV1Api;

    public SecretDeleter(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String namespace, String secret) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(secret), () -> "delete resource name is null");
        V1Status v1Status = coreV1Api.deleteNamespacedSecret(
                secret,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced secret success \n '{}'", v1Status);
    }
}

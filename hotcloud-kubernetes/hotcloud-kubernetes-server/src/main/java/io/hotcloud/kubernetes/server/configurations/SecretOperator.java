package io.hotcloud.kubernetes.server.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.api.configurations.SecretApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static io.hotcloud.kubernetes.model.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class SecretOperator implements SecretApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public SecretOperator(CoreV1Api coreV1Api, KubernetesClient fabric8client) {
        this.coreV1Api = coreV1Api;
        this.fabric8client = fabric8client;
    }

    @Override
    public Secret secret(String yaml) throws ApiException {

        V1Secret v1Secret;
        try {
            v1Secret = Yaml.loadAs(yaml, V1Secret.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load secret yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Secret.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Secret cm = coreV1Api.createNamespacedSecret(namespace,
                v1Secret,
                "true",
                null,
                null);
        log.debug("create secret success \n '{}'", cm);

        return fabric8client.secrets()
                .inNamespace(namespace)
                .withName(v1Secret.getMetadata().getName())
                .get();
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

    @Override
    public SecretList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8client.secrets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8client.secrets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}

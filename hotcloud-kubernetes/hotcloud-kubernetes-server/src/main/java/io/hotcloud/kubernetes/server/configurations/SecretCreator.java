package io.hotcloud.kubernetes.server.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.HotCloudException;
import io.hotcloud.kubernetes.api.configurations.SecretCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static io.hotcloud.kubernetes.model.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class SecretCreator implements SecretCreateApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public SecretCreator(CoreV1Api coreV1Api, KubernetesClient fabric8client) {
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

        Secret secret = fabric8client.secrets()
                .inNamespace(namespace)
                .withName(v1Secret.getMetadata().getName())
                .get();
        return secret;
    }
}

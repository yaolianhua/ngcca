package io.hotcloud.kubernetes.server.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.HotCloudException;
import io.hotcloud.kubernetes.api.configurations.ConfigMapCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
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
public class ConfigMapCreator implements ConfigMapCreateApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public ConfigMapCreator(CoreV1Api coreV1Api, KubernetesClient fabric8client) {
        this.coreV1Api = coreV1Api;
        this.fabric8client = fabric8client;
    }

    @Override
    public ConfigMap configMap(String yaml) throws ApiException {

        V1ConfigMap v1ConfigMap;
        try {
            v1ConfigMap = Yaml.loadAs(yaml, V1ConfigMap.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load configMap yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1ConfigMap.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1ConfigMap cm = coreV1Api.createNamespacedConfigMap(namespace,
                v1ConfigMap,
                "true",
                null,
                null);
        log.debug("create configMap success \n '{}'", cm);

        ConfigMap configMap = fabric8client.configMaps()
                .inNamespace(namespace)
                .withName(v1ConfigMap.getMetadata().getName())
                .get();
        return configMap;
    }
}

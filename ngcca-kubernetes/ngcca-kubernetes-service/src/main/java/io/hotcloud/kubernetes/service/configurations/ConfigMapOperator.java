package io.hotcloud.kubernetes.service.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.ConfigMapApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class ConfigMapOperator implements ConfigMapApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public ConfigMapOperator(CoreV1Api coreV1Api, KubernetesClient fabric8client) {
        this.coreV1Api = coreV1Api;
        this.fabric8client = fabric8client;
    }

    @Override
    public ConfigMap create(String yaml) throws ApiException {

        V1ConfigMap v1ConfigMap;
        try {
            v1ConfigMap = Yaml.loadAs(yaml, V1ConfigMap.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load configMap yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1ConfigMap.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1ConfigMap cm = coreV1Api.createNamespacedConfigMap(namespace,
                v1ConfigMap,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create configMap '%s' success", Objects.requireNonNull(cm.getMetadata()).getName()));

        return fabric8client.configMaps()
                .inNamespace(namespace)
                .withName(v1ConfigMap.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String configmap) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(configmap, () -> "delete resource name is null");
        coreV1Api.deleteNamespacedConfigMap(
                configmap,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        Log.debug(this, null, String.format("delete '%s' namespaced configMap '%s' success", namespace, configmap));
    }

    @Override
    public ConfigMapList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8client.configMaps()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8client.configMaps()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}

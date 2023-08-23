package io.hotcloud.kubernetes.service.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.DaemonSetApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class DaemonSetOperator implements DaemonSetApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DaemonSetOperator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public DaemonSet create(String yaml) throws ApiException {
        V1DaemonSet v1DaemonSet;
        try {
            v1DaemonSet = Yaml.loadAs(yaml, V1DaemonSet.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load daemonSet yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1DaemonSet.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1DaemonSet created = appsV1Api.createNamespacedDaemonSet(namespace,
                v1DaemonSet,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create daemonSet '%s' success", Objects.requireNonNull(created.getMetadata()).getName()));

        return fabric8Client.apps()
                .daemonSets()
                .inNamespace(namespace)
                .withName(v1DaemonSet.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String daemonSet) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(daemonSet, () -> "delete resource name is null");
        appsV1Api.deleteNamespacedDaemonSet(
                daemonSet,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        Log.debug(this, null, String.format("delete '%s' namespaced daemonSet '%s' success", namespace, daemonSet));
    }

    @Override
    public DaemonSetList read(String namespace, Map<String, String> labelSelector) {

        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.apps()
                    .daemonSets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.apps()
                .daemonSets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}

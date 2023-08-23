package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.StatefulSetApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class StatefulSetOperator implements StatefulSetApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public StatefulSetOperator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public StatefulSet create(String yaml) throws ApiException {
        V1StatefulSet v1StatefulSet;
        try {
            v1StatefulSet = Yaml.loadAs(yaml, V1StatefulSet.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load statefulSet yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1StatefulSet.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1StatefulSet created = appsV1Api.createNamespacedStatefulSet(namespace,
                v1StatefulSet,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create statefulSet '%s' success", Objects.requireNonNull(created.getMetadata()).getName()));

        return fabric8Client.apps()
                .statefulSets()
                .inNamespace(namespace)
                .withName(v1StatefulSet.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String statefulSet) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(statefulSet, () -> "delete resource name is null");
        appsV1Api.deleteNamespacedStatefulSet(
                statefulSet,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        Log.debug(this, null, String.format("delete '%s' namespaced statefulSet '%s' success", namespace, statefulSet));
    }

    @Override
    public StatefulSetList read(String namespace, Map<String, String> labelSelector) {

        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.apps()
                    .statefulSets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.apps()
                .statefulSets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}

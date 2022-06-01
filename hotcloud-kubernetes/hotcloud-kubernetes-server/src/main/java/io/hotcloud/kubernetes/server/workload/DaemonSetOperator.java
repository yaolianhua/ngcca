package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.api.UUIDGenerator;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.kubernetes.api.workload.DaemonSetApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;


/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DaemonSetOperator implements DaemonSetApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DaemonSetOperator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public DaemonSet daemonSet(String yaml) throws ApiException {
        V1DaemonSet v1DaemonSet;
        try {
            v1DaemonSet = Yaml.loadAs(yaml, V1DaemonSet.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load daemonSet yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1DaemonSet.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : UUIDGenerator.DEFAULT;
        V1DaemonSet created = appsV1Api.createNamespacedDaemonSet(namespace,
                v1DaemonSet,
                "true",
                null,
                null, null);
        log.debug("create daemonSet success \n '{}'", created);

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
        V1Status v1Status = appsV1Api.deleteNamespacedDaemonSet(
                daemonSet,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        log.debug("delete namespaced daemonSet success \n '{}'", v1Status);
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

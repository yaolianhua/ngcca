package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.workload.StatefulSetCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

import static io.hotcloud.core.kubernetes.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class StatefulSetCreator implements StatefulSetCreateApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public StatefulSetCreator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public StatefulSet statefulSet(String yaml) throws ApiException {
        V1StatefulSet v1StatefulSet;
        try {
            v1StatefulSet = (V1StatefulSet) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load statefulSet yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1StatefulSet.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1StatefulSet created = appsV1Api.createNamespacedStatefulSet(namespace,
                v1StatefulSet,
                "true",
                null,
                null);
        log.debug("create statefulSet success \n '{}'", created);

        StatefulSet statefulSet = fabric8Client.apps()
                .statefulSets()
                .inNamespace(namespace)
                .withName(v1StatefulSet.getMetadata().getName())
                .get();
        return statefulSet;
    }
}

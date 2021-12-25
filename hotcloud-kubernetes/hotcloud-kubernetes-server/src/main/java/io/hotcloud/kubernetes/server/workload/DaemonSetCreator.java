package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.HotCloudException;
import io.hotcloud.kubernetes.api.workload.DaemonSetCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
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
public class DaemonSetCreator implements DaemonSetCreateApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DaemonSetCreator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
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
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1DaemonSet created = appsV1Api.createNamespacedDaemonSet(namespace,
                v1DaemonSet,
                "true",
                null,
                null);
        log.debug("create daemonSet success \n '{}'", created);

        DaemonSet daemonSet = fabric8Client.apps()
                .daemonSets()
                .inNamespace(namespace)
                .withName(v1DaemonSet.getMetadata().getName())
                .get();
        return daemonSet;
    }
}

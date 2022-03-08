package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.api.pod.PodCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static io.hotcloud.kubernetes.model.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class PodCreator implements PodCreateApi {


    private final KubernetesClient fabric8Client;
    private final CoreV1Api coreV1Api;

    public PodCreator(KubernetesClient fabric8Client, CoreV1Api coreV1Api) {
        this.fabric8Client = fabric8Client;
        this.coreV1Api = coreV1Api;
    }

    @Override
    public Pod pod(String yaml) throws ApiException {
        V1Pod v1Pod;
        try {
            v1Pod = Yaml.loadAs(yaml, V1Pod.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load pod yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Pod.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Pod pod = coreV1Api.createNamespacedPod(
                namespace,
                v1Pod,
                "true",
                null,
                null);
        log.debug("create pod success \n '{}'", pod);

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(v1Pod.getMetadata().getName())
                .get();
    }
}

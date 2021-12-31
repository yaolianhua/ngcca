package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.pod.PodDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodDeleter implements PodDeleteApi {

    private final KubernetesClient fabric8Client;
    private final CoreV1Api coreV1Api;

    public PodDeleter(KubernetesClient fabric8Client, CoreV1Api coreV1Api) {
        this.fabric8Client = fabric8Client;
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void delete(String namespace, String pod) throws ApiException {

        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(pod), () -> "delete resource name is null");
        V1Pod v1Pod = coreV1Api.deleteNamespacedPod(pod, namespace, "true",
                null,
                null,
                null,
                "Foreground",
                null);

        log.debug("delete namespaced pod success \n '{}'", v1Pod);

    }

    @Override
    public void delete(String namespace, Map<String, String> label) {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        label = label == null ? Map.of() : label;
        Boolean delete = fabric8Client.pods()
                .inNamespace(namespace)
                .withLabels(label)
                .delete();
        log.debug("delete labeled '{}' pod success '{}'", label, delete);
    }
}

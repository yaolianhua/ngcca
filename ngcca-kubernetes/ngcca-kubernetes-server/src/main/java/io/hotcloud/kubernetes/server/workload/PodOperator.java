package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.PodApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodOperator implements PodApi {

    private final KubernetesClient fabric8Client;
    private final CoreV1Api coreV1Api;

    public PodOperator(KubernetesClient fabric8Client, CoreV1Api coreV1Api) {
        this.fabric8Client = fabric8Client;
        this.coreV1Api = coreV1Api;
    }

    @Override
    public Pod create(String yaml) throws ApiException {
        V1Pod v1Pod;
        try {
            v1Pod = Yaml.loadAs(yaml, V1Pod.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load pod yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Pod.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1Pod pod = coreV1Api.createNamespacedPod(
                namespace,
                v1Pod,
                "true",
                null,
                null, null);
        log.debug("create pod success \n '{}'", pod);

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(v1Pod.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String pod) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(pod, () -> "delete resource name is null");
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
        Assert.hasText(namespace, () -> "namespace is null");
        label = label == null ? Map.of() : label;
        List<StatusDetails> details = fabric8Client.pods()
                .inNamespace(namespace)
                .withLabels(label)
                .delete();
        log.debug("delete labeled '{}' pod success '{}'", label, details.size());
    }

    @Override
    public String logs(String namespace, String pod, Integer tailingLine) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(pod, () -> "pod name is null");

        tailingLine = tailingLine == null ? Integer.MAX_VALUE : tailingLine;

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .tailingLines(tailingLine)
                .getLog(true);
    }

    @Override
    public String logs(String namespace, String pod, String container, Integer tailingLine) {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(pod, () -> "pod name is null");
        Assert.hasText(container, () -> "container name is null");

        tailingLine = tailingLine == null ? Integer.MAX_VALUE : tailingLine;

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .inContainer(container)
                .tailingLines(tailingLine)
                .getLog(true);
    }

    @Override
    public PodList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.pods()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.pods()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public Pod addAnnotations(String namespace, String pod, Map<String, String> annotations) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.state(!CollectionUtils.isEmpty(annotations), "annotations is empty");

        Pod newPod = fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .edit(
                        p -> new PodBuilder(p)
                                .editMetadata()
                                .addToAnnotations(annotations)
                                .endMetadata()
                                .build()
                );
        log.debug("Annotated annotations '{}' for pod '{}' in '{}'", annotations, pod, namespace);
        return newPod;
    }

    @Override
    public Pod addLabels(String namespace, String pod, Map<String, String> labels) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.state(!CollectionUtils.isEmpty(labels), "labels is empty");

        Pod newPod = fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .edit(
                        p -> new PodBuilder(p)
                                .editMetadata()
                                .addToLabels(labels)
                                .endMetadata()
                                .build()
                );
        log.debug("Tag labels '{}' for pod '{}' in '{}'", labels, pod, namespace);
        return newPod;
    }
}

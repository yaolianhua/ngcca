package io.hotcloud.kubernetes.service.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.PodApi;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
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
        Log.debug(this, yaml, String.format("create pod '%s' success", Objects.requireNonNull(pod.getMetadata()).getName()));

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(v1Pod.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String pod) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
        coreV1Api.deleteNamespacedPod(pod, namespace, "true",
                null,
                null,
                null,
                "Foreground",
                null);

        Log.debug(this, null, String.format("delete '%s' namespaced pod '%s' success", namespace, pod));
    }

    @Override
    public void delete(String namespace, Map<String, String> label) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        label = label == null ? Map.of() : label;
        List<StatusDetails> details = fabric8Client.pods()
                .inNamespace(namespace)
                .withLabels(label)
                .delete();
        Log.debug(this, null, String.format("delete labeled '%s' pod success '%s'", label, details.size()));
    }

    @Override
    public String logs(String namespace, String pod, Integer tailingLine) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);

        tailingLine = tailingLine == null ? Integer.MAX_VALUE : tailingLine;

        return fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .tailingLines(tailingLine)
                .getLog(true);
    }

    @Override
    public String logs(String namespace, String pod, String container, Integer tailingLine) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
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
        Log.debug(this, null, String.format("Annotated annotations '%s' for pod '%s' in '%s'", annotations, pod, namespace));
        return newPod;
    }

    @Override
    public Pod addLabels(String namespace, String pod, Map<String, String> labels) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
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
        Log.debug(this, null, String.format("Tag labels '%s' for pod '%s' in '%s'", labels, pod, namespace));
        return newPod;
    }
}

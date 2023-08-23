package io.hotcloud.kubernetes.server.namespace;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class NamespaceOperator implements NamespaceApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public NamespaceOperator(CoreV1Api coreV1Api,
                             KubernetesClient fabric8client) {
        this.coreV1Api = coreV1Api;
        this.fabric8client = fabric8client;
    }

    @Override
    public void create(NamespaceCreateRequest namespaceCreateRequest) throws ApiException {

        List<V1Namespace> namespaceList = coreV1Api.listNamespace("true",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null).getItems();
        List<String> namespaces = namespaceList.stream()
                .filter(e -> Objects.nonNull(e.getMetadata()))
                .map(e -> e.getMetadata().getName())
                .filter(StringUtils::hasText)
                .toList();

        String name = namespaceCreateRequest.getMetadata().getName();
        if (namespaces.contains(name)) {
            Log.warn(this, namespaceCreateRequest, String.format("namespace '%s' already exists", name));
            return;
        }

        V1Namespace namespace = new V1Namespace();
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setLabels(namespaceCreateRequest.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(namespaceCreateRequest.getMetadata().getAnnotations());
        namespace.setMetadata(v1ObjectMeta);

        namespace.setApiVersion("v1");
        namespace.setKind("Namespace");

        coreV1Api.createNamespace(namespace, "true", null, null, null);
        Log.debug(this, namespaceCreateRequest, String.format("namespace '%s' created", name));
    }

    @Override
    public void delete(String namespace) throws ApiException {
        Assert.hasText(namespace, "namespace is null");
        coreV1Api.deleteNamespace(
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null);
        Log.debug(this, null, String.format("delete namespace '%s' success", namespace));
    }

    @Override
    public NamespaceList read(Map<String, String> labelSelector) {
        labelSelector = labelSelector == null ? Map.of() : labelSelector;
        return fabric8client.namespaces()
                .withLabels(labelSelector)
                .list();
    }
}

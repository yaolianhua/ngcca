package io.hotcloud.kubernetes.server.namespace;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class NamespaceOperator implements NamespaceApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8client;

    public NamespaceOperator(CoreV1Api coreV1Api,
                             KubernetesClient fabric8client) {
        this.coreV1Api = coreV1Api;
        this.fabric8client = fabric8client;
    }

    @Override
    public void namespace(NamespaceCreateRequest namespaceCreateRequest) throws ApiException {

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
                .collect(Collectors.toList());

        String name = namespaceCreateRequest.getMetadata().getName();
        if (namespaces.contains(name)) {
            log.warn("Namespace '{}' already exists", name);
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

        V1Namespace v1Namespace = coreV1Api.createNamespace(namespace, "true", null, null);
        log.debug("Namespace '{}' created \n '{}'", name, v1Namespace);
    }

    @Override
    public void delete(String namespace) throws ApiException {
        Assert.hasText(namespace, "namespace is null", 400);
        V1Status aTrue = coreV1Api.deleteNamespace(
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null);
        log.debug("delete namespace '{}' success \n '{}'", namespace, aTrue);
    }

    @Override
    public NamespaceList read(Map<String, String> labelSelector) {
        labelSelector = labelSelector == null ? Map.of() : labelSelector;
        return fabric8client.namespaces()
                .withLabels(labelSelector)
                .list();
    }
}

package io.hotcloud.kubernetes.server.network;

import com.google.gson.JsonSyntaxException;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.ServiceApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class ServiceOperator implements ServiceApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public ServiceOperator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Service create(String yaml) throws ApiException {

        V1Service v1Service;
        try {
            v1Service = Yaml.loadAs(yaml, V1Service.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load service yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        coreV1Api.createNamespacedService(namespace,
                v1Service,
                "true",
                null,
                null, null);

        return fabric8Client.services()
                .inNamespace(namespace)
                .withName(v1Service.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String service) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(service, () -> "delete resource name is null");
        try {
            coreV1Api.deleteNamespacedService(
                    service,
                    namespace,
                    "true",
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } catch (JsonSyntaxException e) {
            //https://github.com/kubernetes-client/java/issues/86
        }
    }

    @Override
    public ServiceList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client
                    .services()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client
                .services()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}

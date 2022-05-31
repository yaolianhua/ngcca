package io.hotcloud.kubernetes.server.network;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static io.hotcloud.common.UUIDGenerator.DEFAULT;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ServiceOperator implements ServiceApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public ServiceOperator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Service service(String yaml) throws ApiException {

        V1Service v1Service;
        try {
            v1Service = Yaml.loadAs(yaml, V1Service.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load service yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT;
        V1Service service = coreV1Api.createNamespacedService(namespace,
                v1Service,
                "true",
                null,
                null, null);
        log.debug("create service success \n '{}'", service);

        return fabric8Client.services()
                .inNamespace(namespace)
                .withName(v1Service.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String service) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(service, () -> "delete resource name is null");
        V1Service v1Service = coreV1Api.deleteNamespacedService(
                service,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced service success \n '{}'", v1Service);
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

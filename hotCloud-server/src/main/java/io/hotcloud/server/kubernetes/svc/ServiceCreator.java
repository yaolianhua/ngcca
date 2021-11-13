package io.hotcloud.server.kubernetes.svc;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.svc.ServiceCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
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
public class ServiceCreator implements ServiceCreateApi {

    private final CoreV1Api coreV1Api;
    private final KubernetesClient fabric8Client;

    public ServiceCreator(CoreV1Api coreV1Api, KubernetesClient fabric8Client) {
        this.coreV1Api = coreV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Service service(String yaml) throws ApiException {

        V1Service v1Service;
        try {
            v1Service = (V1Service) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load service yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Service service = coreV1Api.createNamespacedService(namespace,
                v1Service,
                "true",
                null,
                null);
        log.debug("create service success \n '{}'", service);

        Service svc = fabric8Client.services()
                .inNamespace(namespace)
                .withName(v1Service.getMetadata().getName())
                .get();
        return svc;
    }
}

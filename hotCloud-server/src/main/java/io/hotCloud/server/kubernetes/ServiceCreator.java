package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.HotCloudException;
import io.hotCloud.core.kubernetes.svc.ServiceCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ServiceCreator implements ServiceCreateApi {

    private final CoreV1Api coreV1Api;

    public ServiceCreator(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public V1Service service(String yaml) throws ApiException {

        V1Service v1Service ;
        try {
            v1Service = (V1Service) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load service yaml error. '%s'",e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        V1Service service = coreV1Api.createNamespacedService(namespace,
                v1Service,
                "true",
                null,
                null);
        return service;
    }
}

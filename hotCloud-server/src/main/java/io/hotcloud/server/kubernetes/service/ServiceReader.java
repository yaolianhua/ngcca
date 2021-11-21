package io.hotcloud.server.kubernetes.service;

import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.service.ServiceReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ServiceReader implements ServiceReadApi {

    private final KubernetesClient fabric8Client;

    public ServiceReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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

        ServiceList serviceList = fabric8Client
                .services()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return serviceList;
    }
}

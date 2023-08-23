package io.hotcloud.kubernetes.service.network;

import io.fabric8.kubernetes.api.model.networking.v1.IngressList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.IngressApi;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IngressOperator implements IngressApi {

    private final KubernetesClient fabric8Client;

    public IngressOperator(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public IngressList read(String namespace) {
        if (!StringUtils.hasText(namespace)) {
            return fabric8Client.network()
                    .v1()
                    .ingresses()
                    .inAnyNamespace()
                    .list();
        }
        return fabric8Client.network()
                .v1()
                .ingresses()
                .inNamespace(namespace)
                .list();
    }
}

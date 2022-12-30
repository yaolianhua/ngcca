package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressList;

import java.util.Objects;

public interface IngressClient {

    /**
     * Read namespaced ingress list
     *
     * @param namespace k8s namespace
     * @return {@link IngressList}
     */
    IngressList readList(String namespace);

    /**
     * Read all ingress
     *
     * @return {@link IngressList}
     */
    IngressList readList();

    /**
     * Read ingress with giving params
     *
     * @param namespace k8s namespace
     * @param name      ingress name
     * @return {@link Ingress}
     */
    default Ingress read(String namespace, String name) {
        IngressList ingressList = this.readList(namespace);
        return ingressList.getItems()
                .parallelStream()
                .filter(i -> Objects.equals(i.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }
}

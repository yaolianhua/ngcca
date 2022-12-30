package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressList;

import java.util.Objects;

public interface IngressApi {
    /**
     * Read all ingress
     *
     * @return {@link IngressList}
     */
    default IngressList read() {
        return this.read(null);
    }

    /**
     * Read namespaced ingress list
     *
     * @param namespace k8s namespace
     * @return {@link IngressList}
     */
    IngressList read(String namespace);

    /**
     * Read ingress with giving params
     *
     * @param namespace k8s namespace
     * @param ingress   ingress name
     * @return {@link Ingress}
     */
    default Ingress read(String namespace, String ingress) {
        IngressList ingressList = this.read(namespace);
        return ingressList.getItems()
                .parallelStream()
                .filter(i -> Objects.equals(i.getMetadata().getName(), ingress))
                .findFirst()
                .orElse(null);
    }
}

package io.hotcloud.core.kubernetes.service;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface ServiceReadApi {

    default Service read(String namespace, String service) {
        ServiceList serviceList = this.read(namespace);
        return serviceList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), service))
                .findFirst()
                .orElse(null);
    }

    default ServiceList read() {
        return this.read(null);
    }

    default ServiceList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    ServiceList read(String namespace, Map<String, String> labelSelector);
}

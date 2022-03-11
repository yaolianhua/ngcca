package io.hotcloud.kubernetes.api.network;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ServiceApi {

    default Service service(ServiceCreateRequest request) throws ApiException {
        V1Service v1Service = ServiceBuilder.build(request);
        String json = Yaml.dump(v1Service);
        return this.service(json);
    }

    Service service(String yaml) throws ApiException;

    void delete(String namespace, String service) throws ApiException;

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

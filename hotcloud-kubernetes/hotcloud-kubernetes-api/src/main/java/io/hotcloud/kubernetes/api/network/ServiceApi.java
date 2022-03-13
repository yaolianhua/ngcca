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
    /**
     * Create Service from {@code ServiceCreateRequest}
     *
     * @param request {@link ServiceCreateRequest}
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Service service(ServiceCreateRequest request) throws ApiException {
        V1Service v1Service = ServiceBuilder.build(request);
        String json = Yaml.dump(v1Service);
        return this.service(json);
    }

    /**
     * Create Service from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Service service(String yaml) throws ApiException;

    /**
     * Delete namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String service) throws ApiException;

    /**
     * Read namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @return {@link Service}
     */
    default Service read(String namespace, String service) {
        ServiceList serviceList = this.read(namespace);
        return serviceList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), service))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read ServiceList all namespace
     *
     * @return {@link ServiceList}
     */
    default ServiceList read() {
        return this.read(null);
    }

    /**
     * Read namespaced ServiceList
     *
     * @param namespace namespace
     * @return {@link ServiceList}
     */
    default ServiceList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced ServiceList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ServiceList}
     */
    ServiceList read(String namespace, Map<String, String> labelSelector);
}

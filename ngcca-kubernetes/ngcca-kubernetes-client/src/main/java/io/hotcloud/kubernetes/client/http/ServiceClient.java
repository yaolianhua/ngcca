package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ServiceClient {

    /**
     * Read namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @return {@link Service}
     */
    Service read(String namespace, String service);

    /**
     * Read namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @return {@link Service}
     */
    Service read(String agent, String namespace, String service);

    /**
     * Read namespaced ServiceList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ServiceList}
     */
    ServiceList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Read namespaced ServiceList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link ServiceList}
     */
    ServiceList readList(String agent, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced ServiceList
     *
     * @return {@link ServiceList}
     */
    ServiceList readList();

    /**
     * Read all namespaced ServiceList
     *
     * @return {@link ServiceList}
     */
    ServiceList readList(String agent);

    /**
     * Create Service from {@code ServiceCreateRequest}
     *
     * @param request {@link ServiceCreateRequest}
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Service create(ServiceCreateRequest request) throws ApiException;

    /**
     * Create Service from {@code ServiceCreateRequest}
     *
     * @param request {@link ServiceCreateRequest}
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Service create(String agent, ServiceCreateRequest request) throws ApiException;

    /**
     * Create Service from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Service create(YamlBody yaml) throws ApiException;

    /**
     * Create Service from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Service}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Service create(String agent, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String service) throws ApiException;

    /**
     * Delete namespaced Service
     *
     * @param namespace namespace
     * @param service   service name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agent, String namespace, String service) throws ApiException;

}

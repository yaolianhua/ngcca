package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DaemonSetClient {

    /**
     * Read namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link DaemonSet}
     */
    default DaemonSet read(String namespace, String daemonSet) {
        return read(null, namespace, daemonSet);
    }

    /**
     * Read namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link DaemonSet}
     */
    DaemonSet read(String agentUrl, String namespace, String daemonSet);

    /**
     * Read namespaced DaemonSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DaemonSetList}
     */
    default DaemonSetList readList(String namespace, Map<String, String> labelSelector) {
        return readList(null, namespace, labelSelector);
    }

    /**
     * Read namespaced DaemonSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DaemonSetList}
     */
    DaemonSetList readList(String agentUrl, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced DaemonSetList
     *
     * @return {@link DaemonSetList}
     */
    default DaemonSetList readList() {
        return readList(null);
    }

    /**
     * Read all namespaced DaemonSetList
     *
     * @return {@link DaemonSetList}
     */
    DaemonSetList readList(String agentUrl);

    /**
     * Create DaemonSet from {@code DaemonSetCreateRequest}
     *
     * @param request {@link DaemonSetCreateRequest}
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default DaemonSet create(DaemonSetCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create DaemonSet from {@code DaemonSetCreateRequest}
     *
     * @param request {@link DaemonSetCreateRequest}
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    DaemonSet create(String agentUrl, DaemonSetCreateRequest request) throws ApiException;

    /**
     * Create DaemonSet from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default DaemonSet create(YamlBody yaml) throws ApiException {
        return create(null, yaml);
    }

    /**
     * Create DaemonSet from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    DaemonSet create(String agentUrl, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String namespace, String daemonSet) throws ApiException {
        return delete(null, namespace, daemonSet);
    }

    /**
     * Delete namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agentUrl, String namespace, String daemonSet) throws ApiException;

}

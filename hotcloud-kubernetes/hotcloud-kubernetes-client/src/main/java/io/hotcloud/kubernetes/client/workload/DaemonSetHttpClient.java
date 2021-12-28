package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DaemonSetHttpClient {

    /**
     * Read namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link Result}
     */
    Result<DaemonSet> read(String namespace, String daemonSet);

    /**
     * Read namespaced DaemonSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link Result}
     */
    Result<DaemonSetList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create DaemonSet from {@code DaemonSetCreateRequest}
     *
     * @param request {@link DaemonSetCreateRequest}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<DaemonSet> create(DaemonSetCreateRequest request) throws ApiException;

    /**
     * Create DaemonSet from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<DaemonSet> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String daemonSet) throws ApiException;

}

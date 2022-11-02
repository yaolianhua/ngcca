package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DaemonSetApi {
    /**
     * Create DaemonSet from {@code DaemonSetCreateRequest}
     *
     * @param request {@link DaemonSetCreateRequest}
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default DaemonSet create(DaemonSetCreateRequest request) throws ApiException {
        V1DaemonSet v1DaemonSet = DaemonSetBuilder.build(request);
        String json = Yaml.dump(v1DaemonSet);
        return this.create(json);
    }

    /**
     * Create DaemonSet from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link DaemonSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    DaemonSet create(String yaml) throws ApiException;

    /**
     * Delete namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String daemonSet) throws ApiException;

    /**
     * Read namespaced DaemonSet
     *
     * @param namespace namespace
     * @param daemonSet daemonSet name
     * @return {@link DaemonSet}
     */
    default DaemonSet read(String namespace, String daemonSet) {
        DaemonSetList daemonSetList = this.read(namespace);
        return daemonSetList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), daemonSet))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read DaemonSetList all namespace
     *
     * @return {@link DaemonSetList}
     */
    default DaemonSetList read() {
        return this.read(null);
    }

    /**
     * Read namespaced DaemonSetList
     *
     * @param namespace namespace
     * @return {@link DaemonSetList}
     */
    default DaemonSetList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced DaemonSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DaemonSetList}
     */
    DaemonSetList read(String namespace, Map<String, String> labelSelector);
}

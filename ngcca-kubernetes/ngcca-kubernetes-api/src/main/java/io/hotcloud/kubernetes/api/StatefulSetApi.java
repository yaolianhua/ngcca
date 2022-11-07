package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.kubernetes.model.workload.StatefulSetBuilder;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface StatefulSetApi {
    /**
     * Create StatefulSet from {@code StatefulSetCreateRequest}
     *
     * @param request {@link StatefulSetCreateRequest}
     * @return {@link StatefulSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default StatefulSet create(StatefulSetCreateRequest request) throws ApiException {
        V1StatefulSet v1StatefulSet = StatefulSetBuilder.build(request);
        String json = Yaml.dump(v1StatefulSet);
        return this.create(json);
    }

    /**
     * Create StatefulSet from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link StatefulSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StatefulSet create(String yaml) throws ApiException;

    /**
     * Delete namespaced StatefulSet
     *
     * @param namespace   namespace
     * @param statefulSet statefulSet name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String statefulSet) throws ApiException;

    /**
     * Read namespaced StatefulSet
     *
     * @param namespace   namespace
     * @param statefulSet statefulSet name
     * @return {@link StatefulSet}
     */
    default StatefulSet read(String namespace, String statefulSet) {
        StatefulSetList statefulSetList = this.read(namespace);
        return statefulSetList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), statefulSet))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read StatefulSetList all namespace
     *
     * @return {@link StatefulSetList}
     */
    default StatefulSetList read() {
        return this.read(null);
    }

    /**
     * Read namespaced StatefulSetList
     *
     * @param namespace namespace
     * @return {@link StatefulSetList}
     */
    default StatefulSetList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced StatefulSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link StatefulSetList}
     */
    StatefulSetList read(String namespace, Map<String, String> labelSelector);
}

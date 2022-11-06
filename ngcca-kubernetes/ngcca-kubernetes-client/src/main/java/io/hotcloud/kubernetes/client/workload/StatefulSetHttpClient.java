package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface StatefulSetHttpClient {

    /**
     * Read namespaced StatefulSet
     *
     * @param namespace   namespace
     * @param statefulSet statefulSet name
     * @return {@link StatefulSet}
     */
    StatefulSet read(String namespace, String statefulSet);

    /**
     * Read namespaced StatefulSetList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link StatefulSetList}
     */
    StatefulSetList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create StatefulSet from {@code StatefulSetCreateRequest}
     *
     * @param request {@link StatefulSetCreateRequest}
     * @return {@link StatefulSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StatefulSet create(StatefulSetCreateRequest request) throws ApiException;

    /**
     * Create StatefulSet from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link StatefulSet}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    StatefulSet create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced StatefulSet
     *
     * @param namespace   namespace
     * @param statefulSet statefulSet name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String statefulSet) throws ApiException;

}

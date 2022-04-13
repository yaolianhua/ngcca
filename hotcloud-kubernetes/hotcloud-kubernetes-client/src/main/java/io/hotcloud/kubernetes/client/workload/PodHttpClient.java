package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.model.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodHttpClient {

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    Result<String> logs(String namespace, String pod, Integer tail);

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link List}
     */
    Result<List<String>> loglines(String namespace, String pod, Integer tail);

    /**
     * Read namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Pod}
     */
    Result<Pod> read(String namespace, String pod);

    /**
     * Read namespaced PodList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PodList}
     */
    Result<PodList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create Pod from {@code PodCreateRequest}
     *
     * @param request {@link PodCreateRequest}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Pod> create(PodCreateRequest request) throws ApiException;

    /**
     * Annotate annotations for pod
     * <p> Equivalent of `kubectl annotate pods my-pod icon-url=http://goo.gl/XXBTWq`
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param annotations map of annotations
     * @return {@link Pod}
     */
    Result<Pod> addAnnotations(String namespace, String pod, Map<String, String> annotations);

    /**
     * Tag labels for pod
     * <p> Equivalent of `kubectl label pods my-pod new-label=awesome`
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param labels    map of labels
     * @return {@link Pod}
     */
    Result<Pod> addLabels(String namespace, String pod, Map<String, String> labels);

    /**
     * Create Pod from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Pod> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String pod) throws ApiException;

}

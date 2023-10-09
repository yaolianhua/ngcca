package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.util.Yaml;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodClient {

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    default String podLogs(String namespace, String pod, Integer tail) {
        return podLogs(null, namespace, pod, tail);
    }

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    String podLogs(String agentUrl, String namespace, String pod, Integer tail);

    /**
     * Get container logs
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param container   container name
     * @param tailingLine tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    default String containerLogs(String namespace, String pod, String container, Integer tailingLine) {
        return containerLogs(null, namespace, pod, container, tailingLine);
    }

    /**
     * Get container logs
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param container   container name
     * @param tailingLine tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    String containerLogs(String agentUrl, String namespace, String pod, String container, Integer tailingLine);

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link List}
     */
    default List<String> podLogList(String namespace, String pod, Integer tail) {
        return podLogList(null, namespace, pod, tail);
    }

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param tail      tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link List}
     */
    List<String> podLogList(String agentUrl, String namespace, String pod, Integer tail);

    /**
     * Read namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Pod}
     */
    default Pod read(String namespace, String pod) {
        return read(null, namespace, pod);
    }

    /**
     * Read namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Pod}
     */
    Pod read(String agentUrl, String namespace, String pod);

    /**
     * Read namespaced pod yaml
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return pod yaml
     */
    default String readYaml(String namespace, String pod) {
        return Yaml.dump(this.read(namespace, pod));
    }

    /**
     * Read namespaced pod yaml
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return pod yaml
     */
    default String readYaml(String agentUrl, String namespace, String pod) {
        return Yaml.dump(this.read(agentUrl, namespace, pod));
    }

    /**
     * Read namespaced PodList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PodList}
     */
    default PodList readList(String namespace, Map<String, String> labelSelector) {
        return readList(null, namespace, labelSelector);
    }

    /**
     * Read namespaced PodList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PodList}
     */
    PodList readList(String agentUrl, String namespace, Map<String, String> labelSelector);

    /**
     * List all namespaced PodList
     *
     * @return {@link PodList}
     */
    default PodList readList() {
        return readList(null);
    }

    /**
     * List all namespaced PodList
     *
     * @return {@link PodList}
     */
    PodList readList(String agentUrl);

    /**
     * Create Pod from {@code PodCreateRequest}
     *
     * @param request {@link PodCreateRequest}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Pod create(PodCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create Pod from {@code PodCreateRequest}
     *
     * @param request {@link PodCreateRequest}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Pod create(String agentUrl, PodCreateRequest request) throws ApiException;

    /**
     * Annotate annotations for pod
     * <p> Equivalent of `kubectl annotate pods my-pod icon-url=http://goo.gl/XXBTWq`
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param annotations map of annotations
     * @return {@link Pod}
     */
    default Pod addAnnotations(String namespace, String pod, Map<String, String> annotations) {
        return addAnnotations(null, namespace, pod, annotations);
    }

    /**
     * Annotate annotations for pod
     * <p> Equivalent of `kubectl annotate pods my-pod icon-url=http://goo.gl/XXBTWq`
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param annotations map of annotations
     * @return {@link Pod}
     */
    Pod addAnnotations(String agentUrl, String namespace, String pod, Map<String, String> annotations);

    /**
     * Tag labels for pod
     * <p> Equivalent of `kubectl label pods my-pod new-label=awesome`
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param labels    map of labels
     * @return {@link Pod}
     */
    default Pod addLabels(String namespace, String pod, Map<String, String> labels) {
        return addLabels(null, namespace, pod, labels);
    }

    /**
     * Tag labels for pod
     * <p> Equivalent of `kubectl label pods my-pod new-label=awesome`
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param labels    map of labels
     * @return {@link Pod}
     */
    Pod addLabels(String agentUrl, String namespace, String pod, Map<String, String> labels);

    /**
     * Create Pod from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Pod create(YamlBody yaml) throws ApiException {
        return create(null, yaml);
    }

    /**
     * Create Pod from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Pod create(String agentUrl, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String namespace, String pod) throws ApiException {
        return delete(null, namespace, pod);
    }

    /**
     * Delete namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agentUrl, String namespace, String pod) throws ApiException;

}

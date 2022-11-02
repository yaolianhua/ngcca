package io.hotcloud.kubernetes.api.pod;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodApi {
    /**
     * Create Pod from {@code PodCreateRequest}
     *
     * @param request {@link PodCreateRequest}
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Pod create(PodCreateRequest request) throws ApiException {
        V1Pod v1Pod = PodBuilder.build(request);
        String json = Yaml.dump(v1Pod);
        return this.create(json);
    }

    /**
     * Create Pod from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link Pod}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Pod create(String yaml) throws ApiException;

    /**
     * Delete namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String pod) throws ApiException;

    /**
     * Delete namespaced Pod
     *
     * @param namespace namespace
     * @param label     label selector
     */
    void delete(String namespace, Map<String, String> label);

    /**
     * Get namespaced pod logs
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link String}
     */
    default String logs(String namespace, String pod) {
        return this.logs(namespace, pod, null);
    }

    /**
     * Get namespaced pod logs
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param tailingLine tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    String logs(String namespace, String pod, Integer tailingLine);

    /**
     * Get container logs
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param container   container name
     * @param tailingLine tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link String}
     */
    String logs(String namespace, String pod, String container, Integer tailingLine);

    /**
     * Get namespaced pod logs
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param tailingLine tail numbers of logs, it will be set {@code Integer.MAX_VALUE } if null
     * @return {@link List}
     */
    default List<String> logsline(String namespace, String pod, Integer tailingLine) {
        String log = logs(namespace, pod, tailingLine);
        return Stream.of(log.split("\n")).collect(Collectors.toList());
    }

    /**
     * Read namespaced Pod
     *
     * @param namespace namespace
     * @param pod       pod name
     * @return {@link Pod}
     */
    default Pod read(String namespace, String pod) {
        PodList podList = this.read(namespace);
        return podList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), pod))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read PodList all namespace
     *
     * @return {@link PodList}
     */
    default PodList read() {
        return this.read(null);
    }

    /**
     * Read namespaced PodList
     *
     * @param namespace namespace
     * @return {@link PodList}
     */
    default PodList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced PodList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PodList}
     */
    PodList read(String namespace, Map<String, String> labelSelector);

    /**
     * Annotate annotations for pod
     * <p> Equivalent of `kubectl annotate pods my-pod icon-url=http://goo.gl/XXBTWq`
     *
     * @param namespace   namespace
     * @param pod         pod name
     * @param annotations map of annotations
     * @return {@link Pod}
     */
    Pod addAnnotations(String namespace, String pod, Map<String, String> annotations);

    /**
     * Tag labels for pod
     * <p> Equivalent of `kubectl label pods my-pod new-label=awesome`
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param labels    map of labels
     * @return {@link Pod}
     */
    Pod addLabels(String namespace, String pod, Map<String, String> labels);

}

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

    default Pod pod(PodCreateRequest request) throws ApiException {
        V1Pod v1Pod = PodBuilder.build(request);
        String json = Yaml.dump(v1Pod);
        return this.pod(json);
    }

    Pod pod(String yaml) throws ApiException;

    void delete(String namespace, String pod) throws ApiException;

    void delete(String namespace, Map<String, String> label);

    default String logs(String namespace, String pod) {
        return this.logs(namespace, pod, null);
    }

    String logs(String namespace, String pod, Integer tailingLine);

    default List<String> logsline(String namespace, String pod, Integer tailingLine) {
        String log = logs(namespace, pod, tailingLine);
        return Stream.of(log.split("\n")).collect(Collectors.toList());
    }

    default Pod read(String namespace, String pod) {
        PodList podList = this.read(namespace);
        return podList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), pod))
                .findFirst()
                .orElse(null);
    }

    default PodList read() {
        return this.read(null);
    }

    default PodList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    PodList read(String namespace, Map<String, String> labelSelector);

    Pod addAnnotations(String namespace, String pod, Map<String, String> annotations);

    Pod addLabels(String namespace, String pod, Map<String, String> labels);

}

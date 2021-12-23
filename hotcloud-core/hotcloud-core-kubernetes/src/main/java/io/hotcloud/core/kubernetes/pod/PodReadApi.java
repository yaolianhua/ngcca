package io.hotcloud.core.kubernetes.pod;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PodReadApi {

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
}

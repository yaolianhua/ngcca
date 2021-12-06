package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DaemonSetReadApi {
    default DaemonSet read(String namespace, String daemonSet) {
        DaemonSetList daemonSetList = this.read(namespace);
        return daemonSetList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), daemonSet))
                .findFirst()
                .orElse(null);
    }

    default DaemonSetList read() {
        return this.read(null);
    }

    default DaemonSetList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    DaemonSetList read(String namespace, Map<String, String> labelSelector);
}

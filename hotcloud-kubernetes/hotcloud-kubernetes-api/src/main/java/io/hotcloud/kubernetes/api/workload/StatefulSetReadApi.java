package io.hotcloud.kubernetes.api.workload;


import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface StatefulSetReadApi {

    default StatefulSet read(String namespace, String statefulSet) {
        StatefulSetList statefulSetList = this.read(namespace);
        return statefulSetList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), statefulSet))
                .findFirst()
                .orElse(null);
    }

    default StatefulSetList read() {
        return this.read(null);
    }

    default StatefulSetList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    StatefulSetList read(String namespace, Map<String, String> labelSelector);
}

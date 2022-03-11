package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
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

    default StatefulSet statefulSet(StatefulSetCreateRequest request) throws ApiException {
        V1StatefulSet v1StatefulSet = StatefulSetBuilder.build(request);
        String json = Yaml.dump(v1StatefulSet);
        return this.statefulSet(json);
    }

    StatefulSet statefulSet(String yaml) throws ApiException;

    void delete(String namespace, String statefulSet) throws ApiException;

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

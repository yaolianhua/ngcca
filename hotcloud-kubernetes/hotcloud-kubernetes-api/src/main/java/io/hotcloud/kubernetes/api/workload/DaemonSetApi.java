package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DaemonSetApi {

    default DaemonSet daemonSet(DaemonSetCreateRequest request) throws ApiException {
        V1DaemonSet v1DaemonSet = DaemonSetBuilder.build(request);
        String json = Yaml.dump(v1DaemonSet);
        return this.daemonSet(json);
    }

    DaemonSet daemonSet(String yaml) throws ApiException;

    void delete(String namespace, String daemonSet) throws ApiException;

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

package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DaemonSetCreateApi {
    default DaemonSet daemonSet(DaemonSetCreateRequest request) throws ApiException {
        V1DaemonSet v1DaemonSet = DaemonSetBuilder.build(request);
        String json = Yaml.dump(v1DaemonSet);
        return this.daemonSet(json);
    }

    DaemonSet daemonSet(String yaml) throws ApiException;
}

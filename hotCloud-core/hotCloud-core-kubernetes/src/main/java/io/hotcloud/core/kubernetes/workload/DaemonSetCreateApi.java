package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DaemonSetCreateApi {
    default DaemonSet daemonSet(DaemonSetCreateParams request) throws ApiException {
        V1DaemonSet v1DaemonSet = DaemonSetBuilder.build(request);
        String json = Yaml.dump(v1DaemonSet);
        return this.daemonSet(json);
    }

    DaemonSet daemonSet(String yaml) throws ApiException;
}

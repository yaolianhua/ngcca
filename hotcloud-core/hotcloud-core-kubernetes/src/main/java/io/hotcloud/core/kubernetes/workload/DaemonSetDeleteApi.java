package io.hotcloud.core.kubernetes.workload;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface DaemonSetDeleteApi {
    void delete(String namespace, String daemonSet) throws ApiException;
}

package io.hotcloud.core.kubernetes.workload;

import io.kubernetes.client.openapi.ApiException;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JobDeleteApi {

    void delete(String namespace, String job) throws ApiException;

}

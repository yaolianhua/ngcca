package io.hotcloud.core.kubernetes.pod;

import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodDeleteApi {

    void delete(String namespace, String pod) throws ApiException;

    void delete(String namespace, Map<String, String> label);
}

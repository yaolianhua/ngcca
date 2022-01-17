package io.hotcloud.kubernetes.api.pod;

import io.fabric8.kubernetes.client.Watch;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodWatchApi {

    Watch watch(String namespace, Map<String, String> labels);
}

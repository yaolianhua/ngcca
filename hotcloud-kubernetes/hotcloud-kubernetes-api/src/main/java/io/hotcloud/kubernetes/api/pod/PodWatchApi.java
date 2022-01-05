package io.hotcloud.kubernetes.api.pod;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watch;
import io.hotcloud.kubernetes.api.WatchCallback;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodWatchApi {

    Watch watch(String namespace, WatchCallback<Pod> callback);
}

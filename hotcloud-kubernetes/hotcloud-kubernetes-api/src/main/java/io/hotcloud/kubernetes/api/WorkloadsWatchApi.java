package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.client.Watch;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface WorkloadsWatchApi {

    Watch watch();
}

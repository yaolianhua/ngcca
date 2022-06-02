package io.hotcloud.kubernetes.server;

import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        name = KubernetesProperties.ENABLE_WORKLOADS_WATCHER,
        havingValue = "true"
)
@Slf4j
public class WatcherConfiguration {

    private final Map<String, WorkloadsWatchApi> watchApiContainer;

    public WatcherConfiguration(Map<String, WorkloadsWatchApi> watchApiContainer) {
        this.watchApiContainer = watchApiContainer;
    }

    @PostConstruct
    public void init() {
        Log.info(WatcherConfiguration.class.getName(), String.format("【Start workloads watcher '%s'】", watchApiContainer.keySet()));
        for (WorkloadsWatchApi watchApi : watchApiContainer.values()) {
            try {
                watchApi.watch();
            } catch (Exception e) {
                Log.error(WatcherConfiguration.class.getName(), String.format("Start workload watcher error. %s", e.getMessage()));
            }

        }
    }


}

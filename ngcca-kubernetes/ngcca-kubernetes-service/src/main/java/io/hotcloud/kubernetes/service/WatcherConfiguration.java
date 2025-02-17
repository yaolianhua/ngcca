package io.hotcloud.kubernetes.service;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class WatcherConfiguration {

    private final Map<String, WorkloadsWatchApi> watchApiContainer;

    public WatcherConfiguration(Map<String, WorkloadsWatchApi> watchApiContainer) {
        this.watchApiContainer = watchApiContainer;
    }

    @PostConstruct
    public void init() {
        Log.info(this, null, Event.START, String.format("start workloads watcher '%s'", watchApiContainer.keySet()));
        for (WorkloadsWatchApi watchApi : watchApiContainer.values()) {
            try {
                watchApi.watch();
            } catch (Exception e) {
                Log.error(this, null, Event.START, String.format("start workloads watcher error. %s", e.getMessage()));
            }

        }
    }


}

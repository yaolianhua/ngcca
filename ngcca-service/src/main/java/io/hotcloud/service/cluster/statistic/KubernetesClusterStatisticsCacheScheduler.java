package io.hotcloud.service.cluster.statistic;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesClusterStatisticsCacheScheduler {

    private final KubernetesClusterStatisticsService kubernetesClusterStatisticsService;
    private final Cache cache;

    @Scheduled(cron = "*/5 * * * * *")
    public void refresh() {

        Log.debug(this, null, Event.SCHEDULE, "kubernetes cluster statistics cache refresh task is running");


        try {
            AllClusterStatistics statistics = kubernetesClusterStatisticsService.allStatistics();
            cache.put(KubernetesClusterStatisticsService.KUBERNETES_CLUSTER_STATISTICS_KEY, statistics);
        } catch (Exception e) {
            Log.error(this, null, Event.SCHEDULE, "refresh kubernetes cluster statistics cache error: " + e.getMessage());
        }

    }
}

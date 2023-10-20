package io.hotcloud.service.cluster.statistic;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClusterListStatisticsService {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final ClusterStatisticsService clusterStatisticsService;

    public static final String KUBERNETES_CLUSTER_STATISTICS_KEY = "kubernetes:cluster:statistics";
    private final Cache cache;

    public ClusterListStatistics getClusterListStatisticsFromCache() {
        return cache.get(KUBERNETES_CLUSTER_STATISTICS_KEY, this::clusterListStatistics);
    }
    /**
     *
     *
     * @return {@link ClusterListStatistics}
     */
    public ClusterListStatistics clusterListStatistics() {

        List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        List<ClusterStatistics> clusterStatisticsList = kubernetesClusters.stream().map(clusterStatisticsService::clusterStatistics).collect(Collectors.toList());

        return ClusterListStatistics.builder().items(clusterStatisticsList).build();

    }

    /**
     *
     * @return {@link ClusterListStatistics}
     */
    public ClusterListStatistics namespacedClusterListStatistics(String namespace) {

        List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        List<ClusterStatistics> clusterStatisticsList = kubernetesClusters.stream().map(e -> clusterStatisticsService.namespacedClusterStatistics(e, namespace)).collect(Collectors.toList());

        return ClusterListStatistics.builder().items(clusterStatisticsList).build();

    }

}

package io.hotcloud.service.cluster.statistic;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KubernetesClusterStatisticsService {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final WorkloadObjectQueryService workloadObjectQueryService;
    private final PodMetricsQueryService podMetricsQueryService;
    private final NodeMetricsQueryService nodeMetricsQueryService;

    public static final String KUBERNETES_CLUSTER_STATISTICS_KEY = "kubernetes:cluster:statistics";
    private final Cache cache;

    public ClusterListStatistics allCacheStatistics() {
        return cache.get(KUBERNETES_CLUSTER_STATISTICS_KEY, this::allStatistics);
    }
    /**
     *
     *
     * @return {@link ClusterListStatistics}
     */
    public ClusterListStatistics allStatistics() {

        List<ClusterStatistics> clusterStatisticsList = new ArrayList<>();
        final List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        for (KubernetesCluster cluster : kubernetesClusters) {

            ClusterStatistics clusterStatistics = ClusterStatistics.builder()
                    .cluster(cluster)
                    .podMetrics(podMetricsQueryService.listPodMetrics(cluster, null))
                    .nodeMetrics(nodeMetricsQueryService.listNodeMetrics(cluster))
                    .pods(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.POD))
                    .deployments(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DEPLOYMENT))
                    .jobs(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.JOB))
                    .cronJobs(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CRONJOB))
                    .daemonSets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DAEMONSET))
                    .statefulSets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.STATEFULSET))
                    .services(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SERVICE))
                    .secrets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SECRET))
                    .configMaps(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CONFIGMAP))
                    .ingresses(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.INGRESS))
                    .build();

            clusterStatisticsList.add(clusterStatistics);
        }

        return ClusterListStatistics.builder().items(clusterStatisticsList).build();

    }

    /**
     *
     * @return {@link ClusterListStatistics}
     */
    public ClusterListStatistics namespacedStatistics(String namespace) {

        List<ClusterStatistics> clusterStatisticsList = new ArrayList<>();
        List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        for (KubernetesCluster cluster : kubernetesClusters) {

            final ClusterStatistics clusterStatistics = ClusterStatistics.builder()
                    .cluster(cluster)
                    .podMetrics(podMetricsQueryService.listPodMetrics(cluster, namespace))
                    .nodeMetrics(nodeMetricsQueryService.listNodeMetrics(cluster))
                    .pods(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.POD, namespace))
                    .deployments(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DEPLOYMENT, namespace))
                    .jobs(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.JOB, namespace))
                    .cronJobs(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CRONJOB, namespace))
                    .daemonSets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DAEMONSET, namespace))
                    .statefulSets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.STATEFULSET, namespace))
                    .services(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SERVICE, namespace))
                    .secrets(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SECRET, namespace))
                    .configMaps(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CONFIGMAP, namespace))
                    .ingresses(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.INGRESS, namespace))
                    .build();
            clusterStatisticsList.add(clusterStatistics);
        }
        return ClusterListStatistics.builder().items(clusterStatisticsList).build();

    }

}

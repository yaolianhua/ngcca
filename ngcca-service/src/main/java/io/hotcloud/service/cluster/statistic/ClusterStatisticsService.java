package io.hotcloud.service.cluster.statistic;

import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClusterStatisticsService {

    private final WorkloadObjectQueryService workloadObjectQueryService;
    private final PodMetricsQueryService podMetricsQueryService;
    private final NodeMetricsQueryService nodeMetricsQueryService;

    /**
     * @return {@link ClusterStatistics}
     */
    public ClusterStatistics clusterStatistics(KubernetesCluster cluster) {
        return ClusterStatistics.builder()
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
    }

    /**
     * @return {@link ClusterStatistics}
     */
    public ClusterStatistics namespacedClusterStatistics(KubernetesCluster cluster, String namespace) {
        return ClusterStatistics.builder()
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

    }

}

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

    public AllClusterStatistics allCacheStatistics() {
        return cache.get(KUBERNETES_CLUSTER_STATISTICS_KEY, this::allStatistics);
    }
    /**
     *
     *
     * @return {@link AllClusterStatistics}
     */
    public AllClusterStatistics allStatistics() {
        List<WorkloadObject> pods = new ArrayList<>();
        List<WorkloadObject> deployments = new ArrayList<>();
        List<WorkloadObject> jobs = new ArrayList<>();
        List<WorkloadObject> cronjobs = new ArrayList<>();
        List<WorkloadObject> daemonSets = new ArrayList<>();
        List<WorkloadObject> statefulSets = new ArrayList<>();
        List<WorkloadObject> services = new ArrayList<>();
        List<WorkloadObject> secrets = new ArrayList<>();
        List<WorkloadObject> configMaps = new ArrayList<>();
        List<WorkloadObject> ingresses = new ArrayList<>();
        List<io.hotcloud.service.cluster.statistic.PodMetrics> podMetrics = new ArrayList<>();
        List<io.hotcloud.service.cluster.statistic.NodeMetrics> nodeMetrics = new ArrayList<>();

        final List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        for (KubernetesCluster cluster : kubernetesClusters) {

            pods.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.POD));
            deployments.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DEPLOYMENT));
            jobs.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.JOB));
            cronjobs.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CRONJOB));
            daemonSets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DAEMONSET));
            statefulSets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.STATEFULSET));
            services.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SERVICE));
            secrets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SECRET));
            configMaps.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CONFIGMAP));
            ingresses.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.INGRESS));

            List<PodMetrics> clusterPodMetricsList = podMetricsQueryService.listPodMetrics(cluster, null);
            podMetrics.addAll(clusterPodMetricsList);

            List<NodeMetrics> clusterNodeMetricsList = nodeMetricsQueryService.listNodeMetrics(cluster);
            nodeMetrics.addAll(clusterNodeMetricsList);

        }

        return AllClusterStatistics.builder()
                .clusters(kubernetesClusters)
                .podMetrics(podMetrics)
                .nodeMetrics(nodeMetrics)
                .pods(pods)
                .deployments(deployments)
                .jobs(jobs)
                .cronJobs(cronjobs)
                .daemonSets(daemonSets)
                .statefulSets(statefulSets)
                .services(services)
                .secrets(secrets)
                .configMaps(configMaps)
                .ingresses(ingresses)
                .build();

    }

    /**
     *
     * @return {@link AllClusterStatistics}
     */
    public AllClusterStatistics namespacedStatistics(String namespace) {

        List<WorkloadObject> pods = new ArrayList<>();
        List<WorkloadObject> deployments = new ArrayList<>();
        List<WorkloadObject> jobs = new ArrayList<>();
        List<WorkloadObject> cronjobs = new ArrayList<>();
        List<WorkloadObject> daemonSets = new ArrayList<>();
        List<WorkloadObject> statefulSets = new ArrayList<>();
        List<WorkloadObject> services = new ArrayList<>();
        List<WorkloadObject> secrets = new ArrayList<>();
        List<WorkloadObject> configMaps = new ArrayList<>();
        List<WorkloadObject> ingresses = new ArrayList<>();
        List<io.hotcloud.service.cluster.statistic.PodMetrics> podMetrics = new ArrayList<>();
        List<io.hotcloud.service.cluster.statistic.NodeMetrics> nodeMetrics = new ArrayList<>();

        final List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.listHealth();

        for (KubernetesCluster cluster : kubernetesClusters) {
            pods.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.POD, namespace));
            deployments.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DEPLOYMENT, namespace));
            jobs.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.JOB, namespace));
            cronjobs.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CRONJOB, namespace));
            daemonSets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.DAEMONSET, namespace));
            statefulSets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.STATEFULSET, namespace));
            services.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SERVICE, namespace));
            secrets.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.SECRET, namespace));
            configMaps.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.CONFIGMAP, namespace));
            ingresses.addAll(workloadObjectQueryService.listWorkloadObjects(cluster, WorkloadObjectType.INGRESS, namespace));


            List<PodMetrics> clusterNamespacedPodMetricsList = podMetricsQueryService.listPodMetrics(cluster, namespace);
            podMetrics.addAll(clusterNamespacedPodMetricsList);

            List<NodeMetrics> clusterNodeMetricsList = nodeMetricsQueryService.listNodeMetrics(cluster);
            nodeMetrics.addAll(clusterNodeMetricsList);

        }


        return AllClusterStatistics.builder()
                .clusters(kubernetesClusters)
                .podMetrics(podMetrics)
                .nodeMetrics(nodeMetrics)
                .pods(pods)
                .deployments(deployments)
                .jobs(jobs)
                .cronJobs(cronjobs)
                .daemonSets(daemonSets)
                .statefulSets(statefulSets)
                .services(services)
                .secrets(secrets)
                .configMaps(configMaps)
                .ingresses(ingresses)
                .build();

    }

}

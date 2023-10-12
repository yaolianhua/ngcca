package io.hotcloud.service.cluster.statistic;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.security.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KubernetesClusterStatisticsService {

    private final KubectlClient kubectlClient;
    private final PodClient podClient;
    private final DeploymentClient deploymentClient;
    private final CronJobClient cronJobClient;
    private final JobClient jobClient;
    private final DaemonSetClient daemonSetClient;
    private final StatefulSetClient statefulSetClient;
    private final ServiceClient serviceClient;
    private final ConfigMapClient configMapClient;
    private final SecretClient secretClient;
    private final IngressClient ingressClient;
    private final UserApi userApi;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final WorkloadObjectQueryService workloadObjectQueryService;

    /**
     *
     *
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics allStatistics() {
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


            //
            try {
                List<io.hotcloud.service.cluster.statistic.PodMetrics> podMetricsList = kubectlClient.topPods(cluster.getAgentUrl())
                        .parallelStream()
                        .map(e -> this.build(cluster, e))
                        .toList();

                podMetrics.addAll(podMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
            }

            //
            try {

                List<io.hotcloud.service.cluster.statistic.NodeMetrics> nodeMetricsList = kubectlClient.topNodes(cluster.getAgentUrl())
                        .stream()
                        .map(e -> this.build(cluster, e))
                        .toList();

                nodeMetrics.addAll(nodeMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get node metrics statistics error: " + e.getMessage());
            }
        }


        return KubernetesClusterStatistics.builder()
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
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics namespacedStatistics(String namespace) {

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
            //
            try {
                final List<io.hotcloud.service.cluster.statistic.PodMetrics> podMetricsList = kubectlClient.topNamespacedPods(cluster.getAgentUrl(), namespace)
                        .parallelStream()
                        .map(e -> this.build(cluster, e))
                        .toList();

                podMetrics.addAll(podMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
            }
            //
            try {
                final List<io.hotcloud.service.cluster.statistic.NodeMetrics> nodeMetricsList = kubectlClient.topNodes(cluster.getAgentUrl())
                        .stream()
                        .map(e -> this.build(cluster, e))
                        .toList();

                nodeMetrics.addAll(nodeMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get node metrics statistics error: " + e.getMessage());
            }
        }


        return KubernetesClusterStatistics.builder()
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

    private io.hotcloud.service.cluster.statistic.NodeMetrics build(KubernetesCluster cluster, NodeMetrics fabric8NodeMetrics) {

        String node = fabric8NodeMetrics.getMetadata().getName();
        Node fabric8Node = kubectlClient.getNode(cluster.getAgentUrl(), node);
        NodeStatus nodeStatus = fabric8Node.getStatus();

        long cpuMilliCoresCapacity = Math.round(nodeStatus.getCapacity().get("cpu").getNumericalAmount().doubleValue() * 1000);

        long memoryMegabyteCapacity = Math.round(nodeStatus.getCapacity().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));

        long cpuMilliCoresUsage = Math.round(fabric8NodeMetrics.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000);

        long memoryMegabyteUsage = Math.round(fabric8NodeMetrics.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));

        NodeAddress internalAddress = nodeStatus.getAddresses().stream()
                .filter(e -> "InternalIP".equals(e.getType()))
                .findFirst()
                .orElse(null);
        NodeCondition nodeConditionReady = nodeStatus.getConditions().stream()
                .filter(e -> "Ready".equals(e.getType()) && "True".equals(e.getStatus()))
                .findFirst()
                .orElse(null);


        return io.hotcloud.service.cluster.statistic.NodeMetrics.builder()
                .cluster(cluster)
                .node(node)
                .labels(fabric8Node.getMetadata().getLabels())
                .ip(internalAddress == null ? "unknown" : internalAddress.getAddress())
                .status(nodeConditionReady == null ? "unknown" : "Ready")
                .architecture(nodeStatus.getNodeInfo().getArchitecture())
                .osImage(nodeStatus.getNodeInfo().getOsImage())
                .containerRuntime(nodeStatus.getNodeInfo().getContainerRuntimeVersion())
                .kubeletVersion(nodeStatus.getNodeInfo().getKubeletVersion())
                .cpuMilliCoresCapacity(cpuMilliCoresCapacity)
                .memoryMegabyteCapacity(memoryMegabyteCapacity)
                .cpuMilliCoresUsage(cpuMilliCoresUsage)
                .memoryMegabyteUsage(memoryMegabyteUsage)
                .build();

    }

    private io.hotcloud.service.cluster.statistic.PodMetrics build(KubernetesCluster cluster, PodMetrics fabric8PodMetrics) {
        String pod = fabric8PodMetrics.getMetadata().getName();
        String namespace = fabric8PodMetrics.getMetadata().getNamespace();

        Double cpu = fabric8PodMetrics.getContainers().stream()
                .map(e -> e.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000)
                .reduce(0.0, Double::sum);

        Double memory = fabric8PodMetrics.getContainers().stream()
                .map(e -> e.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024))
                .reduce(0.0, Double::sum);

        Pod podInfo = podClient.read(cluster.getAgentUrl(), namespace, pod);
        io.hotcloud.service.cluster.statistic.PodMetrics.RefNode refNode = io.hotcloud.service.cluster.statistic.PodMetrics.RefNode.builder()
                .ip(podInfo.getStatus().getHostIP())
                .name(podInfo.getSpec().getNodeName())
                .build();

        //
        List<io.fabric8.kubernetes.api.model.Service> serviceList = serviceClient.readList(cluster.getAgentUrl(), namespace, Map.of()).getItems();
        Set<io.hotcloud.service.cluster.statistic.PodMetrics.RefService> refServices = new HashSet<>();
        for (io.fabric8.kubernetes.api.model.Service service : serviceList) {
            Map<String, String> podLabels = podInfo.getMetadata().getLabels();
            Map<String, String> serviceSelector = service.getSpec().getSelector();

            for (Map.Entry<String, String> entry : serviceSelector.entrySet()) {
                if (podLabels.containsKey(entry.getKey()) && podLabels.get(entry.getKey()).equals(entry.getValue())) {
                    String ports = service.getSpec().getPorts()
                            .stream()
                            .map(e -> String.format("%s:%s/%s", e.getPort(), e.getNodePort() == null ? "<none>" : e.getNodePort(), e.getProtocol()))
                            .collect(Collectors.joining(","));
                    //
                    io.hotcloud.service.cluster.statistic.PodMetrics.RefService refService = io.hotcloud.service.cluster.statistic.PodMetrics.RefService.builder()
                            .clusterIp(service.getSpec().getClusterIP())
                            .type(service.getSpec().getType())
                            .name(service.getMetadata().getName())
                            .ports(ports)
                            .build();
                    refServices.add(refService);
                }
            }
        }


        List<io.hotcloud.service.cluster.statistic.Container> containers = podInfo.getSpec().getContainers()
                .stream()
                .map(e -> Container.builder().name(e.getName()).build())
                .toList();

        return io.hotcloud.service.cluster.statistic.PodMetrics.builder()
                .namespace(namespace)
                .pod(pod)
                .containers(containers)
                .status(podInfo.getStatus().getPhase())
                .refNode(refNode)
                .cluster(cluster)
                .refServices(refServices)
                .cpuMilliCoresUsage(Math.round(cpu))
                .memoryMegabyteUsage(Math.round(memory))
                .build();

    }

}

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

    /**
     * 管理员视图数据
     *
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics statistics() {
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

        for (KubernetesCluster kubernetesCluster : databasedKubernetesClusterService.listHealth()) {
            String agent = kubernetesCluster.getAgentUrl();
            //all pods
            try {
                List<WorkloadObject> podList = podClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());

                pods.addAll(podList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get pod statistics error: " + e.getMessage());
            }

            //all deployment
            try {
                List<WorkloadObject> deploymentList = deploymentClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());

                deployments.addAll(deploymentList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get  deployment statistics error: " + e.getMessage());
            }

            //all jobs
            try {
                final List<WorkloadObject> jobList = jobClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                jobs.addAll(jobList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get job statistics error: " + e.getMessage());
            }

            //all cronjobs
            try {
                final List<WorkloadObject> cronjobList = cronJobClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                cronjobs.addAll(cronjobList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get cronjob statistics error: " + e.getMessage());
            }

            //all daemonSets
            try {
                final List<WorkloadObject> daemonSetList = daemonSetClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                daemonSets.addAll(daemonSetList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get daemonset statistics error: " + e.getMessage());
            }

            //
            try {
                final List<WorkloadObject> statefulSetList = statefulSetClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                statefulSets.addAll(statefulSetList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get statefulset statistics error: " + e.getMessage());
            }

            //
            try {
                final List<WorkloadObject> serviceList = serviceClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                services.addAll(serviceList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get service statistics error: " + e.getMessage());
            }

            //
            try {
                final List<WorkloadObject> secretList = secretClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                secrets.addAll(secretList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get secret statistics error: " + e.getMessage());
            }

            //
            try {
                final List<WorkloadObject> configMapList = configMapClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                configMaps.addAll(configMapList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get configmap statistics error: " + e.getMessage());
            }

            //
            try {
                final List<WorkloadObject> ingressList = ingressClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                ingresses.addAll(ingressList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get ingress statistics error: " + e.getMessage());
            }

            //
            try {
                final List<io.hotcloud.service.cluster.statistic.PodMetrics> podMetricsList = kubectlClient.topPods(agent)
                        .parallelStream()
                        .map(e -> this.build(kubernetesCluster, e))
                        .collect(Collectors.toList());

                podMetrics.addAll(podMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
            }

            //
            try {

                final List<io.hotcloud.service.cluster.statistic.NodeMetrics> nodeMetricsList = kubectlClient.topNodes(agent)
                        .stream()
                        .map(e -> this.build(kubernetesCluster, e))
                        .collect(Collectors.toList());

                nodeMetrics.addAll(nodeMetricsList);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "get node metrics statistics error: " + e.getMessage());
            }
        }


        return KubernetesClusterStatistics.builder()
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
     * 用户视图数据
     *
     * @param username 用户名
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics statistics(String username) {

        String namespace = userApi.retrieve(username).getNamespace();

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

        try {
            pods = podClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod statistics error: " + e.getMessage());
        }
        //
        try {
            deployments = deploymentClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get  deployment statistics error: " + e.getMessage());
        }
        //
        try {
            jobs = jobClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get job statistics error: " + e.getMessage());
        }
        //
        try {
            cronjobs = cronJobClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get cronjob statistics error: " + e.getMessage());
        }
        //
        try {
            daemonSets = daemonSetClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get daemonset statistics error: " + e.getMessage());
        }
        //
        try {
            statefulSets = statefulSetClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get statefulset statistics error: " + e.getMessage());
        }
        //
        try {
            services = serviceClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get service statistics error: " + e.getMessage());
        }
        //
        try {
            secrets = secretClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get secret statistics error: " + e.getMessage());
        }
        //
        try {
            configMaps = configMapClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get configmap statistics error: " + e.getMessage());
        }
        //
        try {
            ingresses = ingressClient.readNamespacedList(namespace)
                    .getItems()
                    .parallelStream()
                    .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get ingress statistics error: " + e.getMessage());
        }
        //
        try {
            podMetrics = kubectlClient.topNamespacedPods(namespace)
                    .parallelStream()
                    .map(e -> this.build(new KubernetesCluster(), e))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
        }
        //
        try {
            nodeMetrics = kubectlClient.topNodes()
                    .stream()
                    .map(e -> this.build(new KubernetesCluster(), e))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get node metrics statistics error: " + e.getMessage());
        }

        return KubernetesClusterStatistics.builder()
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

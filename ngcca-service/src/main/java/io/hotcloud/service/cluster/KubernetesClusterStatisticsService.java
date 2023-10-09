package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.service.security.user.UserApi;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
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

    public KubernetesClusterStatisticsService(KubectlClient kubectlClient,
                                              PodClient podClient,
                                              DeploymentClient deploymentClient,
                                              CronJobClient cronJobClient,
                                              JobClient jobClient,
                                              DaemonSetClient daemonSetClient,
                                              StatefulSetClient statefulSetClient,
                                              ServiceClient serviceClient,
                                              ConfigMapClient configMapClient,
                                              SecretClient secretClient,
                                              IngressClient ingressClient,
                                              UserApi userApi) {
        this.kubectlClient = kubectlClient;
        this.podClient = podClient;
        this.deploymentClient = deploymentClient;
        this.cronJobClient = cronJobClient;
        this.jobClient = jobClient;
        this.daemonSetClient = daemonSetClient;
        this.statefulSetClient = statefulSetClient;
        this.serviceClient = serviceClient;
        this.configMapClient = configMapClient;
        this.secretClient = secretClient;
        this.ingressClient = ingressClient;
        this.userApi = userApi;
    }

    /**
     * 管理员视图数据
     *
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics statistics() {
        List<KubernetesClusterStatistics.Pod> pods = new ArrayList<>();
        List<KubernetesClusterStatistics.Deployment> deployments = new ArrayList<>();
        List<KubernetesClusterStatistics.Job> jobs = new ArrayList<>();
        List<KubernetesClusterStatistics.Cronjob> cronjobs = new ArrayList<>();
        List<KubernetesClusterStatistics.DaemonSet> daemonSets = new ArrayList<>();
        List<KubernetesClusterStatistics.StatefulSet> statefulSets = new ArrayList<>();
        List<KubernetesClusterStatistics.Service> services = new ArrayList<>();
        List<KubernetesClusterStatistics.Secret> secrets = new ArrayList<>();
        List<KubernetesClusterStatistics.ConfigMap> configMaps = new ArrayList<>();
        List<KubernetesClusterStatistics.Ingress> ingresses = new ArrayList<>();
        List<KubernetesClusterStatistics.PodMetrics> podMetrics = new ArrayList<>();
        List<KubernetesClusterStatistics.NodeMetrics> nodeMetrics = new ArrayList<>();

        try {
            pods = podClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod statistics error: " + e.getMessage());
        }
        //
        try {
            deployments = deploymentClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Deployment.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get  deployment statistics error: " + e.getMessage());
        }
        //
        try {
            jobs = jobClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Job.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get job statistics error: " + e.getMessage());
        }
        //
        try {
            cronjobs = cronJobClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Cronjob.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get cronjob statistics error: " + e.getMessage());
        }
        //
        try {
            daemonSets = daemonSetClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.DaemonSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get daemonset statistics error: " + e.getMessage());
        }
        //
        try {
            statefulSets = statefulSetClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.StatefulSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get statefulset statistics error: " + e.getMessage());
        }
        //
        try {
            services = serviceClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Service.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get service statistics error: " + e.getMessage());
        }
        //
        try {
            secrets = secretClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Secret.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get secret statistics error: " + e.getMessage());
        }
        //
        try {
            configMaps = configMapClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.ConfigMap.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get configmap statistics error: " + e.getMessage());
        }
        //
        try {
            ingresses = ingressClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Ingress.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get ingress statistics error: " + e.getMessage());
        }
        //
        try {
            podMetrics = kubectlClient.topPods()
                    .parallelStream()
                    .map(this::build)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
        }
        //
        try {
            nodeMetrics = kubectlClient.topNodes()
                    .stream()
                    .map(this::build)
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

    /**
     * 用户视图数据
     *
     * @param username 用户名
     * @return {@link KubernetesClusterStatistics}
     */
    public KubernetesClusterStatistics statistics(String username) {

        String namespace = userApi.retrieve(username).getNamespace();

        List<KubernetesClusterStatistics.Pod> pods = new ArrayList<>();
        List<KubernetesClusterStatistics.Deployment> deployments = new ArrayList<>();
        List<KubernetesClusterStatistics.Job> jobs = new ArrayList<>();
        List<KubernetesClusterStatistics.Cronjob> cronjobs = new ArrayList<>();
        List<KubernetesClusterStatistics.DaemonSet> daemonSets = new ArrayList<>();
        List<KubernetesClusterStatistics.StatefulSet> statefulSets = new ArrayList<>();
        List<KubernetesClusterStatistics.Service> services = new ArrayList<>();
        List<KubernetesClusterStatistics.Secret> secrets = new ArrayList<>();
        List<KubernetesClusterStatistics.ConfigMap> configMaps = new ArrayList<>();
        List<KubernetesClusterStatistics.Ingress> ingresses = new ArrayList<>();
        List<KubernetesClusterStatistics.PodMetrics> podMetrics = new ArrayList<>();
        List<KubernetesClusterStatistics.NodeMetrics> nodeMetrics = new ArrayList<>();

        try {
            pods = podClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod statistics error: " + e.getMessage());
        }
        //
        try {
            deployments = deploymentClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Deployment.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get  deployment statistics error: " + e.getMessage());
        }
        //
        try {
            jobs = jobClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Job.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get job statistics error: " + e.getMessage());
        }
        //
        try {
            cronjobs = cronJobClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Cronjob.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get cronjob statistics error: " + e.getMessage());
        }
        //
        try {
            daemonSets = daemonSetClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.DaemonSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get daemonset statistics error: " + e.getMessage());
        }
        //
        try {
            statefulSets = statefulSetClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.StatefulSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get statefulset statistics error: " + e.getMessage());
        }
        //
        try {
            services = serviceClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Service.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get service statistics error: " + e.getMessage());
        }
        //
        try {
            secrets = secretClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Secret.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get secret statistics error: " + e.getMessage());
        }
        //
        try {
            configMaps = configMapClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.ConfigMap.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get configmap statistics error: " + e.getMessage());
        }
        //
        try {
            ingresses = ingressClient.readList(namespace)
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Ingress.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get ingress statistics error: " + e.getMessage());
        }
        //
        try {
            podMetrics = kubectlClient.topNamespacedPods(namespace)
                    .parallelStream()
                    .map(this::build)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get pod metrics statistics error: " + e.getMessage());
        }
        //
        try {
            nodeMetrics = kubectlClient.topNodes()
                    .stream()
                    .map(this::build)
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

    private KubernetesClusterStatistics.NodeMetrics build(NodeMetrics fabric8NodeMetrics) {

        String node = fabric8NodeMetrics.getMetadata().getName();
        Node fabric8Node = kubectlClient.getNode(node);
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


        return KubernetesClusterStatistics.NodeMetrics.builder()
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

    private KubernetesClusterStatistics.PodMetrics build(PodMetrics fabric8PodMetrics) {
        String pod = fabric8PodMetrics.getMetadata().getName();
        String namespace = fabric8PodMetrics.getMetadata().getNamespace();

        Double cpu = fabric8PodMetrics.getContainers().stream()
                .map(e -> e.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000)
                .reduce(0.0, Double::sum);

        Double memory = fabric8PodMetrics.getContainers().stream()
                .map(e -> e.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024))
                .reduce(0.0, Double::sum);

        Pod podInfo = podClient.read(namespace, pod);
        KubernetesClusterStatistics.PodMetrics.RefNode refNode = KubernetesClusterStatistics.PodMetrics.RefNode.builder()
                .ip(podInfo.getStatus().getHostIP())
                .name(podInfo.getSpec().getNodeName())
                .build();

        //
        List<io.fabric8.kubernetes.api.model.Service> serviceList = serviceClient.readList(namespace, Map.of()).getItems();
        Set<KubernetesClusterStatistics.PodMetrics.RefService> refServices = new HashSet<>();
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
                    KubernetesClusterStatistics.PodMetrics.RefService refService = KubernetesClusterStatistics.PodMetrics.RefService.builder()
                            .clusterIp(service.getSpec().getClusterIP())
                            .type(service.getSpec().getType())
                            .name(service.getMetadata().getName())
                            .ports(ports)
                            .build();
                    refServices.add(refService);
                }
            }
        }


        List<KubernetesClusterStatistics.Container> containers = podInfo.getSpec().getContainers()
                .stream()
                .map(e -> KubernetesClusterStatistics.Container.builder().name(e.getName()).build())
                .toList();

        return KubernetesClusterStatistics.PodMetrics.builder()
                .namespace(namespace)
                .pod(pod)
                .containers(containers)
                .status(podInfo.getStatus().getPhase())
                .refNode(refNode)
                .refServices(refServices)
                .cpuMilliCoresUsage(Math.round(cpu))
                .memoryMegabyteUsage(Math.round(memory))
                .build();

    }

}

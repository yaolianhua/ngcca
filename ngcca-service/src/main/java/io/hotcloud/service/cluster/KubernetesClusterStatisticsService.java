package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.module.security.user.UserApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

        List<KubernetesClusterStatistics.Pod> pods = podClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Deployment> deployments = deploymentClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Deployment.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Job> jobs = jobClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Job.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Cronjob> cronjobs = cronJobClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Cronjob.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.DaemonSet> daemonSets = daemonSetClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.DaemonSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.StatefulSet> statefulSets = statefulSetClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.StatefulSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Service> services = serviceClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Service.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Secret> secrets = secretClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Secret.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.ConfigMap> configMaps = configMapClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.ConfigMap.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Ingress> ingresses = ingressClient.readList()
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Ingress.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());

        List<KubernetesClusterStatistics.PodMetrics> podMetrics = kubectlClient.topPod()
                .parallelStream()
                .map(this::build)
                .collect(Collectors.toList());


        List<KubernetesClusterStatistics.NodeMetrics> nodeMetrics = kubectlClient.topNode()
                .stream()
                .map(this::build)
                .collect(Collectors.toList());

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

        List<KubernetesClusterStatistics.Pod> pods = podClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Deployment> deployments = deploymentClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Deployment.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Job> jobs = jobClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Job.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Cronjob> cronjobs = cronJobClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Cronjob.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.DaemonSet> daemonSets = daemonSetClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.DaemonSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.StatefulSet> statefulSets = statefulSetClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.StatefulSet.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Service> services = serviceClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Service.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Secret> secrets = secretClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Secret.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.ConfigMap> configMaps = configMapClient.readList(namespace, Map.of())
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.ConfigMap.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());
        List<KubernetesClusterStatistics.Ingress> ingresses = ingressClient.readList(namespace)
                .getItems()
                .parallelStream()
                .map(e -> KubernetesClusterStatistics.Ingress.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                .collect(Collectors.toList());

        List<KubernetesClusterStatistics.PodMetrics> podMetrics = kubectlClient.topPod(namespace)
                .parallelStream()
                .map(this::build)
                .collect(Collectors.toList());


        List<KubernetesClusterStatistics.NodeMetrics> nodeMetrics = kubectlClient.topNode()
                .stream()
                .map(this::build)
                .collect(Collectors.toList());

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
                .cpuMilliCoresUsage(Math.round(cpu))
                .memoryMegabyteUsage(Math.round(memory))
                .build();

    }

}

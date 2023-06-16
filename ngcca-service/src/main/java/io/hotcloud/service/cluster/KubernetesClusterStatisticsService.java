package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.module.security.user.UserApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KubernetesClusterStatisticsService {

    private final KubectlClient kubectlClient;
    private final PodClient podClient;
    private final UserApi userApi;

    public KubernetesClusterStatisticsService(KubectlClient kubectlClient,
                                              PodClient podClient,
                                              UserApi userApi) {
        this.kubectlClient = kubectlClient;
        this.podClient = podClient;
        this.userApi = userApi;
    }

    public KubernetesClusterStatistics statistics(String username) {

        boolean admin = userApi.isAdmin(username);
        String namespace = userApi.retrieve(username).getNamespace();

        List<KubernetesClusterStatistics.Pod> pods;
        List<KubernetesClusterStatistics.PodMetrics> podMetrics;

        if (admin) {
            pods = podClient.readList()
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());

            podMetrics = kubectlClient.topPod()
                    .parallelStream()
                    .map(this::build)
                    .collect(Collectors.toList());
        } else {
            pods = podClient.readList(namespace, Map.of())
                    .getItems()
                    .parallelStream()
                    .map(e -> KubernetesClusterStatistics.Pod.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                    .collect(Collectors.toList());

            podMetrics = kubectlClient.topPod(namespace)
                    .parallelStream()
                    .map(this::build)
                    .collect(Collectors.toList());
        }

        List<KubernetesClusterStatistics.NodeMetrics> nodeMetrics = kubectlClient.topNode()
                .stream()
                .map(this::build)
                .collect(Collectors.toList());

        return KubernetesClusterStatistics.builder()
                .podMetrics(podMetrics)
                .nodeMetrics(nodeMetrics)
                .pods(pods)
                .build();

    }

    private KubernetesClusterStatistics.NodeMetrics build(NodeMetrics fabric8NodeMetrics) {

        String node = fabric8NodeMetrics.getMetadata().getName();

        long cpuMilliCoresCapacity = Math.round(kubectlClient.getNode(node).getStatus().getCapacity().get("cpu").getNumericalAmount().doubleValue() * 1000);

        long memoryMegabyteCapacity = Math.round(kubectlClient.getNode(node).getStatus().getCapacity().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));

        long cpuMilliCoresUsage = Math.round(fabric8NodeMetrics.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000);

        long memoryMegabyteUsage = Math.round(fabric8NodeMetrics.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));

        return KubernetesClusterStatistics.NodeMetrics.builder()
                .node(node)
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

        return KubernetesClusterStatistics.PodMetrics.builder()
                .namespace(namespace)
                .pod(pod)
                .cpuMilliCoresUsage(Math.round(cpu))
                .memoryMegabyteUsage(Math.round(memory))
                .build();

    }

}

package io.hotcloud.service.cluster.statistic;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PodMetricsQueryService {

    private final PodClient podClient;
    private final ServiceClient serviceClient;
    private final KubectlClient kubectlClient;

    public List<PodMetrics> listPodMetrics(KubernetesCluster cluster, @Nullable String namespaceParameter) {

        List<PodMetrics> result = new ArrayList<>();
        List<io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics> fabric8PodMetricsList;
        List<Pod> clusterPods;
        List<Service> clusterServices;
        //
        try {
            fabric8PodMetricsList = StringUtils.hasText(namespaceParameter)
                    ? kubectlClient.topNamespacedPods(cluster.getAgentUrl(), namespaceParameter)
                    : kubectlClient.topPods(cluster.getAgentUrl());

            clusterPods = podClient.readList(cluster.getAgentUrl()).getItems();
            clusterServices = serviceClient.readList(cluster.getAgentUrl()).getItems();
        } catch (Exception e) {
            Log.error(this, namespaceParameter, Event.EXCEPTION, "[" + cluster.getName() + "]server error: " + e.getMessage());
            return result;
        }

        //
        for (io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics fabric8PodMetrics : fabric8PodMetricsList) {

            try {
                String pod = fabric8PodMetrics.getMetadata().getName();
                String namespace = fabric8PodMetrics.getMetadata().getNamespace();

                Double cpu = fabric8PodMetrics.getContainers().stream()
                        .map(e -> e.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000)
                        .reduce(0.0, Double::sum);

                Double memory = fabric8PodMetrics.getContainers().stream()
                        .map(e -> e.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024))
                        .reduce(0.0, Double::sum);

                Pod podInfo = clusterPods.stream()
                        .filter(e -> Objects.equals(e.getMetadata().getNamespace(), namespace) && Objects.equals(e.getMetadata().getName(), pod))
                        .findFirst()
                        .orElseThrow(() -> new PlatformException("no items matched. namespace: " + namespace + ", pod: " + pod));
                io.hotcloud.service.cluster.statistic.PodMetrics.RefNode refNode = io.hotcloud.service.cluster.statistic.PodMetrics.RefNode.builder()
                        .ip(podInfo.getStatus().getHostIP())
                        .name(podInfo.getSpec().getNodeName())
                        .build();

                //
                List<Service> serviceList = clusterServices.stream()
                        .filter(e -> Objects.equals(e.getMetadata().getName(), namespace))
                        .toList();

                Set<PodMetrics.RefService> refServices = new HashSet<>();
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

                PodMetrics podMetrics = PodMetrics.builder()
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

                result.add(podMetrics);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "[" + cluster.getName() + "]build podMetrics error: " + e.getMessage());
            }


        }

        return result;
    }
}

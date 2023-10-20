package io.hotcloud.service.cluster.statistic;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.service.cluster.KubernetesCluster;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NodeMetricsQueryService {

    private final KubectlClient kubectlClient;

    public NodeMetricsQueryService(KubectlClient kubectlClient) {
        this.kubectlClient = kubectlClient;
    }

    public List<NodeMetrics> listNodeMetrics(KubernetesCluster cluster) {

        List<NodeMetrics> result = new ArrayList<>();
        List<io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics> fabric8NodeMetricsList;
        List<Node> clusterNodes;
        try {
            fabric8NodeMetricsList = kubectlClient.topNodes(cluster.getAgentUrl());
            clusterNodes = kubectlClient.listNode(cluster.getAgentUrl());
        } catch (Exception e) {
            Log.error(this, null, Event.EXCEPTION, "[" + cluster.getName() + "]server error: " + e.getMessage());
            return result;
        }

        for (io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics fabric8NodeMetrics : fabric8NodeMetricsList) {

            try {
                String nodeName = fabric8NodeMetrics.getMetadata().getName();
                Node fabric8Node = clusterNodes.stream()
                        .filter(e -> Objects.equals(e.getMetadata().getName(), nodeName))
                        .findFirst()
                        .orElseThrow(() -> new PlatformException("no items matched. node: " + nodeName));
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

                List<NodeImage> nodeImages = fabric8Node.getStatus().getImages()
                        .stream()
                        .map(containerImage -> NodeImage.builder()
                                .cluster(cluster.getName())
                                .node(nodeName)
                                .names(containerImage.getNames())
                                .sizeBytes(containerImage.getSizeBytes())
                                .build())
                        .collect(Collectors.toList());

                NodeMetrics nodeMetrics = NodeMetrics.builder()
                        .cluster(cluster)
                        .node(nodeName)
                        .labels(fabric8Node.getMetadata().getLabels())
                        .images(nodeImages)
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

                result.add(nodeMetrics);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, "[" + cluster.getName() + "]build nodeMetrics error: " + e.getMessage());
            }


        }

        return result;
    }

}

package io.hotcloud.server.application.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.server.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KubernetesClusterRabbitMQMessageSubscriber {

    private final KubernetesClusterManagement kubernetesClusterManagement;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_KUBERNETES_CLUSTER_AGENT),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_CLUSTER_AGENT)
                    )
            }
    )
    public void subscribe(String message) {
        Log.info(this, message, "Received kubernetes cluster registry message ");
        Map<String, List<Node>> result = convertMessageBody(message).getData();
        List<Node> masters = result.get("masters");
        List<Node> nodes = result.get("nodes");

        List<KubernetesCluster> kubernetesClusters = kubernetesClusterManagement.list();
        List<String> masterIpList = kubernetesClusters.stream()
                .flatMap(e -> e.getMasters().stream())
                .map(io.hotcloud.module.db.core.cluster.Node::getIp)
                .toList();

        KubernetesCluster kubernetesCluster = new KubernetesCluster();

        for (Node master : masters) {

            io.hotcloud.module.db.core.cluster.Node mNode = new io.hotcloud.module.db.core.cluster.Node();
            mNode.setName(master.getMetadata().getName());
            for (NodeAddress address : master.getStatus().getAddresses()) {
                if (Objects.equals(address.getType(), "InternalIP")) {
                    if (masterIpList.contains(address.getAddress())) {
                        return;
                    }
                    mNode.setIp(address.getAddress());
                }
            }
            NodeSystemInfo nodeInfo = master.getStatus().getNodeInfo();
            mNode.setContainerRuntimeVersion(nodeInfo.getContainerRuntimeVersion());
            mNode.setKubeletVersion(nodeInfo.getKubeletVersion());
            mNode.setKubeProxyVersion(nodeInfo.getKubeProxyVersion());

            kubernetesCluster.getMasters().add(mNode);
        }

        for (Node node : nodes) {
            io.hotcloud.module.db.core.cluster.Node nNode = new io.hotcloud.module.db.core.cluster.Node();
            nNode.setName(node.getMetadata().getName());
            for (NodeAddress address : node.getStatus().getAddresses()) {
                if (Objects.equals(address.getType(), "InternalIP")) {
                    nNode.setIp(address.getAddress());
                }
            }
            NodeSystemInfo nodeInfo = node.getStatus().getNodeInfo();
            nNode.setContainerRuntimeVersion(nodeInfo.getContainerRuntimeVersion());
            nNode.setKubeletVersion(nodeInfo.getKubeletVersion());
            nNode.setKubeProxyVersion(nodeInfo.getKubeProxyVersion());

            kubernetesCluster.getNodes().add(nNode);
        }

        kubernetesCluster.setName(UUIDGenerator.uuidNoDash("cluster"));

        kubernetesClusterManagement.save(kubernetesCluster);
    }

    private Message<Map<String, List<Node>>> convertMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException(e.getMessage());
        }
    }
}

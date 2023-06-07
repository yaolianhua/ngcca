package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Message;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.kubernetes.model.K8sAgentCluster;
import io.hotcloud.service.message.MessageObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KubernetesAgentObserver implements MessageObserver {

    private final KubernetesClusterManagement kubernetesClusterManagement;

    @Override
    public void onMessage(Message<?> message) {
        if (message.getData() instanceof K8sAgentCluster k8sAgentCluster) {
            subscribe(k8sAgentCluster);
        }
    }

    public void subscribe(K8sAgentCluster k8sAgentCluster) {

        Log.info(this, k8sAgentCluster, Event.NOTIFY, "received k8s agent cluster info message");
        List<KubernetesCluster> kubernetesClusters = kubernetesClusterManagement.list();
        List<String> masterIpList = kubernetesClusters.stream()
                .flatMap(e -> e.getMasters().stream())
                .map(io.hotcloud.module.db.entity.Node::getIp)
                .toList();

        KubernetesCluster kubernetesCluster = new KubernetesCluster();

        for (Node master : k8sAgentCluster.getMasters()) {

            io.hotcloud.module.db.entity.Node mNode = new io.hotcloud.module.db.entity.Node();
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

        for (Node node : k8sAgentCluster.getNodes()) {
            io.hotcloud.module.db.entity.Node nNode = new io.hotcloud.module.db.entity.Node();
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

}

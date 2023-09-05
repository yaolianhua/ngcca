package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.message.MessageObserver;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.model.K8sAgentCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KubernetesAgentObserver implements MessageObserver {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    @Override
    public void onMessage(Message<?> message) {
        if (message.getData() instanceof K8sAgentCluster k8sAgentCluster) {
            subscribe(k8sAgentCluster);
        }
    }

    public void subscribe(K8sAgentCluster k8sAgentCluster) {

        Log.info(this, k8sAgentCluster, Event.NOTIFY, "received k8s agent cluster info message");

        KubernetesCluster kubernetesCluster = new KubernetesCluster();

        for (Node master : k8sAgentCluster.getMasters()) {

            io.hotcloud.db.entity.Node mNode = new io.hotcloud.db.entity.Node();
            mNode.setName(master.getMetadata().getName());
            for (NodeAddress address : master.getStatus().getAddresses()) {
                if (Objects.equals(address.getType(), "InternalIP")) {
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
            io.hotcloud.db.entity.Node nNode = new io.hotcloud.db.entity.Node();
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

        kubernetesCluster.setName(k8sAgentCluster.getId());

        databasedKubernetesClusterService.saveOrUpdate(kubernetesCluster);
    }

}

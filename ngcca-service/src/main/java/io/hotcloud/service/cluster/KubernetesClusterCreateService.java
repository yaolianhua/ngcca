package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KubernetesClusterCreateService {

    private final KubectlClient kubectlClient;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    private void parameterValidation(KubernetesClusterRequestCreateParameter parameter) {

        if (!StringUtils.hasText(parameter.getHttpEndpoint())) {
            throw new PlatformException("parameter agent http endpoint is null");
        }

        if (!parameter.getHttpEndpoint().startsWith("http://") &&
                !parameter.getHttpEndpoint().startsWith("https://")) {
            throw new PlatformException("parameter agent http endpoint missing protocol http(s)");
        }

    }

    public void createOrUpdate(KubernetesClusterRequestCreateParameter parameter) {

        parameterValidation(parameter);

        try {
            List<Node> allNodeList = kubectlClient.listNode(parameter.getHttpEndpoint());

            List<Node> masters = allNodeList.stream()
                    .filter(e -> e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
                            || e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master"))
                    .toList();

            if (masters.isEmpty()) {
                throw new PlatformException("Master node not found");
            }

            allNodeList.removeAll(masters);

            KubernetesCluster kubernetesCluster = new KubernetesCluster();

            for (Node master : masters) {

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

            for (Node node : allNodeList) {
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

            kubernetesCluster.setId(parameter.getId());
            kubernetesCluster.setName(parameter.getName());
            kubernetesCluster.setAgentUrl(parameter.getHttpEndpoint());
            kubernetesCluster.setHealth(true);
            databasedKubernetesClusterService.saveOrUpdate(kubernetesCluster);
        } catch (Exception e) {
            Log.error(this, parameter, Event.EXCEPTION, e.getMessage());
            throw new PlatformException(e.getMessage());
        }

    }
}

package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KubernetesClusterService {

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

    @NotNull
    private static io.hotcloud.db.entity.Node buildNode(Node master) {
        io.hotcloud.db.entity.Node node = new io.hotcloud.db.entity.Node();
        node.setName(master.getMetadata().getName());
        for (NodeAddress address : master.getStatus().getAddresses()) {
            if (Objects.equals(address.getType(), "InternalIP")) {
                node.setIp(address.getAddress());
            }
        }
        NodeSystemInfo nodeInfo = master.getStatus().getNodeInfo();
        node.setContainerRuntimeVersion(nodeInfo.getContainerRuntimeVersion());
        node.setKubeletVersion(nodeInfo.getKubeletVersion());
        node.setKubeProxyVersion(nodeInfo.getKubeProxyVersion());
        return node;
    }

    public List<Node> listKubernetesMasters(String agentUrl) {
        List<Node> allNodeList = kubectlClient.listNode(agentUrl);

        return allNodeList.stream()
                .filter(e -> e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
                        || e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master"))
                .toList();
    }

    public List<Node> listKubernetesNodes(String agentUrl) {
        List<Node> allNodeList = kubectlClient.listNode(agentUrl);

        List<Node> masters = allNodeList.stream()
                .filter(e -> e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
                        || e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master"))
                .toList();

        allNodeList.removeAll(masters);

        return allNodeList;
    }

    public void createOrUpdate(KubernetesClusterRequestCreateParameter parameter) {

        parameterValidation(parameter);

        try {
            List<Node> masters = this.listKubernetesMasters(parameter.getHttpEndpoint());

            if (masters.isEmpty()) {
                throw new PlatformException("Master node not found");
            }

            KubernetesCluster kubernetesCluster = new KubernetesCluster();
            List<io.hotcloud.db.entity.Node> masterNodes = masters.stream().map(KubernetesClusterService::buildNode).collect(Collectors.toList());
            kubernetesCluster.setMasters(masterNodes);

            List<Node> workNodes = this.listKubernetesNodes(parameter.getHttpEndpoint());
            List<io.hotcloud.db.entity.Node> nodes = workNodes.stream().map(KubernetesClusterService::buildNode).collect(Collectors.toList());
            kubernetesCluster.setNodes(nodes);

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

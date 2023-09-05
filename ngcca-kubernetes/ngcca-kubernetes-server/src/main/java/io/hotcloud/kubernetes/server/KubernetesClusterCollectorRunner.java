package io.hotcloud.kubernetes.server;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.message.MessageBroadcaster;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.model.K8sAgentCluster;
import io.hotcloud.kubernetes.server.config.KubernetesProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KubernetesClusterCollectorRunner implements ApplicationRunner {

    private final KubernetesClusterApiBeanManager kubernetesClusterApiBeanManager;
    private final MessageBroadcaster messageBroadcaster;
    private final KubernetesProperties kubernetesProperties;

    public KubernetesClusterCollectorRunner(KubernetesClusterApiBeanManager kubernetesClusterApiBeanManager,
                                            MessageBroadcaster messageBroadcaster,
                                            KubernetesProperties kubernetesProperties) {
        this.kubernetesClusterApiBeanManager = kubernetesClusterApiBeanManager;
        this.messageBroadcaster = messageBroadcaster;
        this.kubernetesProperties = kubernetesProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        KubernetesClient kubernetesClient = kubernetesClusterApiBeanManager.getBean(kubernetesProperties.getClusterId(), KubernetesClient.class);
        List<Node> allNodeList = kubernetesClient.nodes()
                .list()
                .getItems();

        List<Node> masters = allNodeList.stream()
                .filter(e -> e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
                        || e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master"))
                .toList();

        if (masters.isEmpty()) {
            throw new RuntimeException("Master node not found");
        }

        allNodeList.removeAll(masters);

        K8sAgentCluster agentCluster = K8sAgentCluster.builder()
                .id(kubernetesProperties.getClusterId())
                .masters(masters)
                .nodes(allNodeList)
                .build();
        Message<K8sAgentCluster> message = Message.of(agentCluster);

        messageBroadcaster.broadcast(CommonConstant.MESSAGE_QUEUE_K8S_AGENT, message);
        Log.info(this, message, Event.NOTIFY, "notify cluster info");
    }
}

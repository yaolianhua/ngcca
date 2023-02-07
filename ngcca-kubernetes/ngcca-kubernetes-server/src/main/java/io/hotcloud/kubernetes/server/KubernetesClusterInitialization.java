package io.hotcloud.kubernetes.server;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.model.module.Message;
import io.hotcloud.kubernetes.model.module.RabbitMQConstant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class KubernetesClusterInitialization implements ApplicationRunner {

    private final KubernetesClient kubernetesClient;
    private final KubernetesRabbitmqMessageBroadcaster rabbitmqMessageBroadcaster;

    public KubernetesClusterInitialization(KubernetesClient kubernetesClient,
                                           KubernetesRabbitmqMessageBroadcaster rabbitmqMessageBroadcaster) {
        this.kubernetesClient = kubernetesClient;
        this.rabbitmqMessageBroadcaster = rabbitmqMessageBroadcaster;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Node> nodes = kubernetesClient.nodes()
                .list()
                .getItems();

        List<Node> masters = nodes.stream()
                .filter(e -> e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
                        || e.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master"))
                .toList();

        if (masters.isEmpty()) {
            throw new RuntimeException("Master node not found");
        }

        nodes.removeAll(masters);

        Message<Map<String, List<Node>>> message = Message.of(Map.of("masters", masters, "nodes", nodes));
        rabbitmqMessageBroadcaster.broadcast(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_CLUSTER_AGENT, message);
    }
}

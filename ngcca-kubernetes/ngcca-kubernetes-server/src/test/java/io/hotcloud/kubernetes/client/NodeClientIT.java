package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Node;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.NodeClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@EnableKubernetesAgentClient
public class NodeClientIT extends ClientIntegrationTestBase {

    private static final Map<String, String> LABELS = Map.of("node-name.k8s.io/hostname", "cus-name");

    @Autowired
    private NodeClient nodeClient;

    @Test
    public void read() {
        List<Node> nodes = nodeClient.nodes(null, null).getItems();
        Assertions.assertFalse(nodes.isEmpty());

        String nodeName = nodes.get(0).getMetadata().getName();

        Node node = nodeClient.addLabels(null, nodeName, LABELS);
        Assertions.assertTrue(node.getMetadata().getLabels().containsKey("node-name.k8s.io/hostname"));

        Node node1 = nodeClient.deleteLabels(null, nodeName, LABELS);
        Assertions.assertFalse(node1.getMetadata().getLabels().containsKey("node-name.k8s.io/hostname"));
    }

}

package io.hotcloud.kubernetes.service.node;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.NodeApi;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class NodeOperator implements NodeApi {

    private final KubernetesClient fabric8Client;

    public NodeOperator(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public NodeList nodes(Map<String, String> labels) {
        if (CollectionUtils.isEmpty(labels)) {
            return fabric8Client.nodes().list();
        }
        return fabric8Client.nodes().withLabels(labels).list();
    }

    @Override
    public Node addLabels(String node, Map<String, String> labels) {
        Node k8sNode = this.node(node);
        Assert.notNull(k8sNode, "k8s node is null [" + node + "]");
        Map<String, String> originLabels = k8sNode.getMetadata().getLabels();
        originLabels.putAll(labels);
        return fabric8Client.nodes().resource(k8sNode).patch();
    }

    @Override
    public Node deleteLabels(String node, Map<String, String> labels) {
        Node k8sNode = this.node(node);
        Assert.notNull(k8sNode, "k8s node is null [" + node + "]");
        Map<String, String> originLabels = k8sNode.getMetadata().getLabels();
        labels.keySet().forEach(originLabels::remove);
        return fabric8Client.nodes().resource(k8sNode).patch();
    }
}

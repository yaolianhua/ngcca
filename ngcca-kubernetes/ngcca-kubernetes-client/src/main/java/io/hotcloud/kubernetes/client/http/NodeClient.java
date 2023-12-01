package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

public interface NodeClient {

    default Node node(String endpoint, String node) {
        return this.nodes(endpoint, null).getItems().parallelStream()
                .filter(n -> Objects.equals(n.getMetadata().getName(), node))
                .findFirst()
                .orElse(null);
    }

    NodeList nodes(String endpoint, @Nullable Map<String, String> labels);

    Node addLabels(String endpoint, String node, Map<String, String> labels);

    Node deleteLabels(String endpoint, String node, Map<String, String> labels);
}

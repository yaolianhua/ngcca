package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

public interface NodeApi {

    default Node node(String node) {
        return this.nodes(null).getItems().parallelStream()
                .filter(n -> Objects.equals(n.getMetadata().getName(), node))
                .findFirst()
                .orElse(null);
    }

    NodeList nodes(@Nullable Map<String, String> labels);

    Node addLabels(String node, Map<String, String> labels);

    Node deleteLabels(String node, Map<String, String> labels);
}

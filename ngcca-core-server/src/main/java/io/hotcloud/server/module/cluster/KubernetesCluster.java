package io.hotcloud.server.module.cluster;

import io.hotcloud.module.db.core.cluster.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KubernetesCluster {

    private String id;
    private String name;
    private List<Node> masters = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();

}

package io.hotcloud.service.cluster;

import io.hotcloud.db.entity.Node;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class KubernetesCluster implements Serializable {

    private String id;
    private String name;
    private String agentUrl;
    private boolean health;
    private List<Node> masters = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();

}

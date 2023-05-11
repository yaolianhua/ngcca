package io.hotcloud.kubernetes.model;

import io.fabric8.kubernetes.api.model.Node;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class K8sAgentCluster implements Serializable {

    @Builder.Default
    private List<Node> masters = new ArrayList<>();

    @Builder.Default
    private List<Node> nodes = new ArrayList<>();

}

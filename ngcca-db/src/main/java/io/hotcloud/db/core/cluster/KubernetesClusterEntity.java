package io.hotcloud.db.core.cluster;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "kubernetes_cluster")
@Getter
@Setter
public class KubernetesClusterEntity extends AbstractEntity {

    @Indexed(unique = true)
    private String name;

    private List<Node> masters = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();

}

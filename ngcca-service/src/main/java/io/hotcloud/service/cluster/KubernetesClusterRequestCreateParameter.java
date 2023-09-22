package io.hotcloud.service.cluster;

import lombok.Data;

@Data
public class KubernetesClusterRequestCreateParameter {

    private String id;
    private String name;
    private String httpEndpoint;
}

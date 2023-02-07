package io.hotcloud.db.core.cluster;

import lombok.Data;

@Data
public class Node {
    private String name;
    private String ip;
    private String kubeletVersion;
    private String kubeProxyVersion;
    private String containerRuntimeVersion;
}

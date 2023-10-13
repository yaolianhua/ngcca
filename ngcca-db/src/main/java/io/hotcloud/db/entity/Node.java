package io.hotcloud.db.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Node implements Serializable {
    private String name;
    private String ip;
    private String kubeletVersion;
    private String kubeProxyVersion;
    private String containerRuntimeVersion;
}

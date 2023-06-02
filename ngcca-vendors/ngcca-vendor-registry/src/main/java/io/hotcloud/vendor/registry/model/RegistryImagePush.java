package io.hotcloud.vendor.registry.model;

import lombok.Data;

@Data
public class RegistryImagePush {

    /**
     * 全名称镜像 e.g. harbor.local:5000/library/registry:2.8.2
     */
    private String name;

    private RegistryType registryType;

    private RegistryAuthentication authentication;
}

package io.hotcloud.vendor.registry.model;

import lombok.Data;

@Data
public class RegistryImage {

    /**
     * 全名称镜像 e.g. harbor.local:5000/library/registry:2.8.2
     */
    private String name;

    private RegistryAuthentication authentication;

    public String getRegistry() {
        return RegistryUtil.getRegistry(this.name);
    }

    public String getNamespacedImageName() {
        return RegistryUtil.getNamespacedImageName(this.name);
    }

    public String getImageTag() {
        return RegistryUtil.getImageTag(this.name);
    }

    public String getRegistryImageWithNoTag() {
        String registry = getRegistry();
        String namespacedImageName = getNamespacedImageName();
        return String.format("%s/%s", registry, namespacedImageName);
    }
}

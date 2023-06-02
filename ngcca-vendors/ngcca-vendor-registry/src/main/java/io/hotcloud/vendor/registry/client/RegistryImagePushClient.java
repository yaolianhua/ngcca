package io.hotcloud.vendor.registry.client;

import io.hotcloud.vendor.registry.model.RegistryImagePush;

public interface RegistryImagePushClient {

    /**
     * 镜像推送到目标仓库
     *
     * @param source 源镜像参数对象
     * @param target 目标镜像参数对象
     */
    void push(RegistryImagePush source, RegistryImagePush target);
}

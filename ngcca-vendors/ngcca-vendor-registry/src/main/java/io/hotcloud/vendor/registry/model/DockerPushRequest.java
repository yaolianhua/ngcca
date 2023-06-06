package io.hotcloud.vendor.registry.model;

import lombok.Data;

@Data
public class DockerPushRequest {

    private RegistryImage source;

    private RegistryImage target;
}

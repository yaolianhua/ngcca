package io.hotcloud.vendor.registry.model;

import io.hotcloud.common.model.exception.PlatformException;

import java.util.Arrays;

public enum RegistryType {
    //
    HARBOR,
    //
    DOCKER_REGISTRY,
    //
    DOCKER_HUB,
    //
    QUAY;

    public static RegistryType of(String name) {
        return Arrays.stream(RegistryType.values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new PlatformException("Unsupported registry type: " + name));
    }
}

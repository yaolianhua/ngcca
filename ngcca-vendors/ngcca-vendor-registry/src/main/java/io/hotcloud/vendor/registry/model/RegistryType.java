package io.hotcloud.vendor.registry.model;

import io.hotcloud.common.model.exception.NGCCAPlatformException;

import java.util.Arrays;

public enum RegistryType {
    //
    Harbor,
    //
    Registry,
    //
    DockerHub,
    //
    Quay;

    public static RegistryType of(String name) {
        return Arrays.stream(RegistryType.values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NGCCAPlatformException("Unsupported registry type: " + name));
    }
}

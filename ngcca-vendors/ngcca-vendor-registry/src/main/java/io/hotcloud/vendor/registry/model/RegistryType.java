package io.hotcloud.vendor.registry.model;

import io.hotcloud.common.model.exception.NGCCACommonException;

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
                .orElseThrow(() -> new NGCCACommonException("Unsupported registry type: " + name));
    }
}

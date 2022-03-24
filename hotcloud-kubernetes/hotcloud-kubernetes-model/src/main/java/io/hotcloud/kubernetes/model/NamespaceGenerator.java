package io.hotcloud.kubernetes.model;

import io.hotcloud.common.Assert;

import java.util.UUID;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class NamespaceGenerator {

    public static final String DEFAULT_NAMESPACE = "default";

    private NamespaceGenerator() {
    }

    public static String defaultNamespace() {
        return DEFAULT_NAMESPACE;
    }

    public static String uuidDashNamespace() {
        return UUID.randomUUID().toString();
    }

    public static String uuidDashNamespace(String prefix) {
        Assert.hasText(prefix, "prefix is null", 400);
        return String.format("%s-%s", prefix, uuidDashNamespace());
    }

    public static String uuidNoDashNamespace() {
        return uuidDashNamespace().replaceAll("-", "");
    }

    public static String uuidNoDashNamespace(String prefix) {
        Assert.hasText(prefix, "prefix is null", 400);
        return String.format("%s-%s", prefix, uuidNoDashNamespace());
    }

}

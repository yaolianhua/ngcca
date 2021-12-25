package io.hotcloud.kubernetes.model;

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

    public static String uuidNamespace() {
        return UUID.randomUUID().toString();
    }

    public static String randomNumber32Bit() {
        return uuidNamespace().replaceAll("-", "");
    }

}

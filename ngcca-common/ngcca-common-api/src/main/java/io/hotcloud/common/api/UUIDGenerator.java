package io.hotcloud.common.api;

import org.springframework.util.Assert;

import java.util.UUID;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class UUIDGenerator {

    public static final String DEFAULT = "default";

    private UUIDGenerator() {
    }

    public static String defaultString() {
        return DEFAULT;
    }

    public static String uuidDashed() {
        return UUID.randomUUID().toString();
    }

    public static String uuidDashed(String prefix) {
        Assert.hasText(prefix, "prefix is null");
        return String.format("%s-%s", prefix, uuidDashed());
    }

    public static String uuidNoDash() {
        return uuidDashed().replaceAll("-", "");
    }

    public static String uuidNoDash(String prefix) {
        Assert.hasText(prefix, "prefix is null");
        return String.format("%s-%s", prefix, uuidNoDash());
    }

}

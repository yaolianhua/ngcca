package io.hotcloud.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

public final class DomainUtils {

    public static String generateDomain(String prefix, String dotSuffix) {
        return prefix + "-" + RandomStringUtils.randomAlphabetic(6).toLowerCase() + dotSuffix;
    }
}

package io.hotcloud.common;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class Validator {

    private Validator() {
    }

    public static boolean validIpv4(String inetAddress) {
        Assert.hasText(inetAddress, "inetAddress is null", 400);
        return InetAddressValidator.getInstance().isValid(inetAddress);
    }

    public static boolean existedPath(String path) {
        Assert.hasText(path, "path is null", 400);
        return Path.of(path).toFile().exists();
    }

    public static boolean validHTTPSGitAddress(String url) {
        Assert.hasText(url, "url is null", 400);
        return new UrlValidator(new String[]{"https"}).isValid(url);
    }
}

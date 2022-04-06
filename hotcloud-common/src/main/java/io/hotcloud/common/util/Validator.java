package io.hotcloud.common.util;

import io.hotcloud.common.Assert;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.file.Path;
import java.util.regex.Pattern;

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

    public static boolean validHTTPGitAddress(String url) {
        Assert.hasText(url, "url is null", 400);
        boolean valid = new UrlValidator(new String[]{"https", "http"}).isValid(url);
        return url.endsWith(".git") && valid;
    }

    final static Pattern USERNAME_PATTERN = Pattern.compile("^[a-z][a-z0-9]{4,15}$");

    public static boolean validUsername(String username) {
        Assert.hasText(username, "username is null", 400);
        return USERNAME_PATTERN.matcher(username).matches();
    }
}

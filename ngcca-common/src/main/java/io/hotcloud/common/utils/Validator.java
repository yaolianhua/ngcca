package io.hotcloud.common.utils;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class Validator {

    private Validator() {
    }

    public static boolean validIpv4(String inetAddress) {
        Assert.hasText(inetAddress, "inetAddress is null");
        return InetAddressValidator.getInstance().isValid(inetAddress);
    }

    public static boolean validHTTPGitAddress(String url) {
        Assert.hasText(url, "url is null");
        boolean endsWithGit = url.endsWith(".git");
        if (!endsWithGit) {
            return false;
        }

        boolean valid = new UrlValidator(new String[]{"https", "http"}).isValid(url);
        if (!valid && (url.startsWith("http://") || url.startsWith("https://"))) {
            String host = INet.getHost(url);
            if (Objects.equals(INet.getLoopbackAddress(), INet.getIPv4(host)) ||
                    Objects.equals(INet.getLocalizedIPv4(), INet.getIPv4(host))) {
                return true;
            }
        }
        return valid;
    }

    final static Pattern USERNAME_PATTERN = Pattern.compile("^[a-z][a-z0-9]{4,15}$");

    public static boolean validUsername(String username) {
        Assert.hasText(username, "username is null");
        return USERNAME_PATTERN.matcher(username).matches();
    }

    final static Pattern K8S_NAME_PATTERN = Pattern.compile("[a-z0-9]([-a-z0-9]*[a-z0-9])?");
    public static boolean validK8sName(String name){
        return K8S_NAME_PATTERN.matcher(name).matches();
    }
}

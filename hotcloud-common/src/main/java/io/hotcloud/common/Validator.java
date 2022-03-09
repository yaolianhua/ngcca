package io.hotcloud.common;

import org.apache.commons.validator.routines.InetAddressValidator;

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
}

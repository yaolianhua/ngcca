package io.hotcloud.common.model;

import io.hotcloud.common.model.exception.HotCloudException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class INet {

    public static String getLocalizedIPv4() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new HotCloudException(e.getMessage());
        }
    }

    public static String getIPv4(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            throw new HotCloudException(e.getMessage());
        }
    }

    public static String getLoopbackAddress() {
        return InetAddress.getLoopbackAddress().getHostAddress();
    }

    public static String getLocalhostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new HotCloudException(e.getMessage());
        }
    }

    public static String getHost(String url) {
        URL address;
        try {
            address = new URL(url);
        } catch (MalformedURLException e) {
            throw new HotCloudException(e.getMessage());
        }
        return address.getHost();
    }

}

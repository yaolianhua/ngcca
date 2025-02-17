package io.hotcloud.server.utils;

import io.hotcloud.common.utils.INet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

 class INetTest {

    @Test
    void ipv4() {

       String localhost = INet.getIPv4("localhost");
       Assertions.assertEquals("127.0.0.1", localhost);

       String us = INet.getIPv4("www.baidu.com");
       System.out.println("www.baidu.com: " + us);

       String local = INet.getLocalizedIPv4();
       Assertions.assertNotEquals("127.0.0.1", local);

        String loopbackAddress = INet.getLoopbackAddress();
        Assertions.assertEquals("127.0.0.1", loopbackAddress);

        String localhostAddress = INet.getLocalhostAddress();
        System.out.println("localhost address: " + localhostAddress);
    }

    @Test
    void host() {
       String host = INet.getHost("https://file.docker.local/Downloads/jenkins.war");
       Assertions.assertEquals("file.docker.local", host);
    }
}

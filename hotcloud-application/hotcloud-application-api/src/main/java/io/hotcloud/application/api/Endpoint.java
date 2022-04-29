package io.hotcloud.application.api;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Endpoint {

    private String protocol;
    private String host;
    private Integer port;

    public static Endpoint of(String protocol, String host, Integer port) {
        Endpoint endpoint = new Endpoint();
        endpoint.setProtocol(protocol);
        endpoint.setHost(host);
        endpoint.setPort(port);
        return endpoint;
    }

    public String getUrl() {
        return String.format("%s://%s:%s", protocol.toLowerCase(), host, port);
    }
}

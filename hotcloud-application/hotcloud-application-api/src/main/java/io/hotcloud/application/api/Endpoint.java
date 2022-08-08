package io.hotcloud.application.api;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Endpoint {

    private String service;
    private String host;
    private String ports;
    private String httpPort;

    public static Endpoint of(String service, String host, String ports, String httpPort) {
        Endpoint endpoint = new Endpoint();
        endpoint.setService(service);
        endpoint.setPorts(ports);
        endpoint.setHost(host);
        endpoint.setHttpPort(httpPort);
        return endpoint;
    }

}

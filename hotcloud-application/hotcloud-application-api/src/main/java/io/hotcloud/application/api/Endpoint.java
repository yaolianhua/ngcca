package io.hotcloud.application.api;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Endpoint {

    private String host;
    private String ports;

    public static Endpoint of(String host, String ports) {
        Endpoint endpoint = new Endpoint();
        endpoint.setHost(host);
        endpoint.setPorts(ports);
        return endpoint;
    }

}

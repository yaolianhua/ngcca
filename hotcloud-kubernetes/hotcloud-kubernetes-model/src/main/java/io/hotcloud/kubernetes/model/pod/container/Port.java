package io.hotcloud.kubernetes.model.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Port {

    private Integer containerPort;

    private PortProtocol protocol = PortProtocol.TCP;
    private String hostIp;
    private Integer hostPort;
    private String name;

}

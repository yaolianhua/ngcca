package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class Port{

    private Integer containerPort;
    @Builder.Default
    private PortProtocol protocol = PortProtocol.TCP;
    private String hostIp;
    private Integer hostPort;
    private String name;

}

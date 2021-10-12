package io.hotCloud.core.kubernetes;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class ServicePort {

    private String appProtocol;
    private String name;
    private Integer nodePort;
    private Integer port;
    private String targetPort;

    @Builder.Default
    private Protocol protocol = Protocol.TCP;

    public enum Protocol{
        //
        TCP,UDP,SCTP
    }
}

package io.hotcloud.kubernetes.model.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class TCPSocket{
    private String host;
    private Integer port;
}

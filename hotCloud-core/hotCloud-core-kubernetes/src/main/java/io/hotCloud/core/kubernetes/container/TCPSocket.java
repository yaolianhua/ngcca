package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Builder
@Data
public class TCPSocket{
    private String host;
    private Integer port;
}

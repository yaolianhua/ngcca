package io.hotCloud.core.kubernetes.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Probe {

    private int failureThreshold;
    private int initialDelaySeconds;
    private int periodSeconds;
    private int successThreshold;
    private int timeoutSeconds;
    private TCPSocket tcpSocket;
    private HttpGet httpGet;
    private Exec exec;
}

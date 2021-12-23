package io.hotcloud.core.kubernetes.pod.container;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ProbeBuilder {

    private ProbeBuilder() {
    }

    public static V1Probe build(Probe probe) {

        V1Probe v1Probe = new V1Probe();
        TCPSocket tcpSocket = probe.getTcpSocket();
        if (Objects.nonNull(tcpSocket)) {
            V1TCPSocketAction tcpSocketAction = new V1TCPSocketAction();
            tcpSocketAction.setHost(tcpSocket.getHost());
            tcpSocketAction.setPort(new IntOrString(tcpSocket.getPort()));
            v1Probe.setTcpSocket(tcpSocketAction);
        }
        Exec exec = probe.getExec();
        if (Objects.nonNull(exec)) {
            V1ExecAction v1ExecAction = new V1ExecAction();
            v1ExecAction.setCommand(exec.getCommand());
            v1Probe.setExec(v1ExecAction);
        }
        HttpGet httpGet = probe.getHttpGet();
        if (Objects.nonNull(httpGet)) {
            V1HTTPGetAction httpGetAction = new V1HTTPGetAction();
            httpGetAction.setHost(httpGet.getHost());
            httpGetAction.setPort(new IntOrString(httpGet.getPort()));
            httpGetAction.setPath(httpGet.getPath());

            List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders().stream()
                    .map(httpHeader -> {
                        V1HTTPHeader v1HTTPHeader = new V1HTTPHeader();
                        v1HTTPHeader.setName(httpHeader.getName());
                        v1HTTPHeader.setValue(httpHeader.getValue());
                        return v1HTTPHeader;
                    }).collect(Collectors.toList());
            httpGetAction.setHttpHeaders(httpHeaders);

            v1Probe.setHttpGet(httpGetAction);
        }

        v1Probe.setPeriodSeconds(probe.getPeriodSeconds());
        v1Probe.setSuccessThreshold(probe.getSuccessThreshold());
        v1Probe.setTimeoutSeconds(probe.getTimeoutSeconds());
        v1Probe.setInitialDelaySeconds(probe.getInitialDelaySeconds());
        v1Probe.setFailureThreshold(probe.getFailureThreshold());

        return v1Probe;
    }
}

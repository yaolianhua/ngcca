package io.hotcloud.kubernetes.api.pod.container;

import io.hotcloud.kubernetes.model.pod.container.Port;
import io.kubernetes.client.openapi.models.V1ContainerPort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ContainerPortBuilder {

    private ContainerPortBuilder() {
    }

    public static V1ContainerPort build(Port port) {

        V1ContainerPort v1ContainerPort = new V1ContainerPort();
        v1ContainerPort.setProtocol(port.getProtocol().name());
        v1ContainerPort.setContainerPort(port.getContainerPort());
        v1ContainerPort.setHostIP(port.getHostIp());
        v1ContainerPort.setHostPort(port.getHostPort());
        v1ContainerPort.setName(port.getName());

        return v1ContainerPort;

    }

    public static List<V1ContainerPort> build(List<Port> ports) {
        return ports.stream().map(ContainerPortBuilder::build).collect(Collectors.toList());
    }
}

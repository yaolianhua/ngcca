package io.hotcloud.kubernetes.api.network;

import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
import io.hotcloud.kubernetes.model.network.ServiceSpec;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ServiceBuilder {

    public static final String KIND = "Service";
    public static final String API_VERSION = "v1";

    private ServiceBuilder() {
    }

    public static V1Service build(ServiceCreateRequest request) {
        final V1Service v1Service = new V1Service();
        v1Service.setKind(KIND);
        v1Service.setApiVersion(API_VERSION);
        ObjectMetadata serviceMetadata = request.getServiceMetadata();
        if (Objects.isNull(serviceMetadata)) {
            throw new RuntimeException("Service metadata can not be null");
        }
        V1ObjectMeta v1ObjectMeta = build(serviceMetadata);
        v1Service.setMetadata(v1ObjectMeta);

        V1ServiceSpec v1ServiceSpec = new V1ServiceSpec();

        ServiceSpec serviceSpec = request.getServiceSpec();
        if (Objects.isNull(serviceSpec)) {
            throw new RuntimeException("Service Spec can not be null");
        }
        List<V1ServicePort> ports = serviceSpec.ports()
                .stream()
                .map(ServiceBuilder::build)
                .collect(Collectors.toList());
        v1ServiceSpec.setPorts(ports);

        v1ServiceSpec.setSelector(serviceSpec.selector());
        v1ServiceSpec.setType(serviceSpec.type());
        v1ServiceSpec.setSessionAffinity(serviceSpec.sessionAffinity());

        v1Service.setSpec(v1ServiceSpec);

        return v1Service;
    }

    private static V1ServicePort build(ServicePort servicePort) {
        V1ServicePort v1ServicePort = new V1ServicePort();
        v1ServicePort.setAppProtocol(servicePort.getAppProtocol());
        v1ServicePort.setName(servicePort.getName());
        v1ServicePort.setNodePort(servicePort.getNodePort());
        v1ServicePort.setProtocol(servicePort.getProtocol().name());
        v1ServicePort.setTargetPort(new IntOrString(servicePort.getTargetPort()));
        v1ServicePort.setPort(servicePort.getPort());
        return v1ServicePort;
    }

    private static V1ObjectMeta build(ObjectMetadata serviceMetadata) {

        Assert.argument(serviceMetadata.getName() != null && !serviceMetadata.getName().isEmpty(), "service name is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(serviceMetadata.getLabels());
        v1ObjectMeta.setName(serviceMetadata.getName());
        v1ObjectMeta.setAnnotations(serviceMetadata.getAnnotations());
        v1ObjectMeta.setNamespace(serviceMetadata.getNamespace());

        return v1ObjectMeta;

    }

}

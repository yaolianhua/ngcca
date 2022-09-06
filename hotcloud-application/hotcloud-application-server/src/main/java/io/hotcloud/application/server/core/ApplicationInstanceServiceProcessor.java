package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.Service;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.network.DefaultServiceSpec;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
import io.hotcloud.kubernetes.model.network.ServiceSpec;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.hotcloud.common.api.CommonConstant.K8S_APP;

@Component
@RequiredArgsConstructor
@Order(3)
class ApplicationInstanceServiceProcessor implements ApplicationInstanceProcessor <ApplicationInstance, Void> {

    private final ServiceApi serviceApi;

    @Override
    public Void process(ApplicationInstance applicationInstance) {

        ServiceCreateRequest request = new ServiceCreateRequest();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setName(applicationInstance.getName());
            metadata.setNamespace(applicationInstance.getNamespace());
            request.setServiceMetadata(metadata);

            DefaultServiceSpec spec = new DefaultServiceSpec();
            spec.setType(ServiceSpec.Type.NodePort);
            spec.setSessionAffinity(ServiceSpec.SessionAffinity.None);
            spec.setSelector(Map.of(K8S_APP, applicationInstance.getName()));

            List<ServicePort> servicePorts = new ArrayList<>();
            for (String port : applicationInstance.getTargetPorts().split(",")) {
                ServicePort servicePort = new ServicePort();
                servicePort.setPort(Integer.parseInt(port));
                servicePorts.add(servicePort);
            }
            spec.setPorts(servicePorts);
            request.setServiceSpec(spec);

            Service svc = serviceApi.service(request);

            String nodePorts = svc.getSpec().getPorts().stream().map(io.fabric8.kubernetes.api.model.ServicePort::getNodePort)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            applicationInstance.setNodePorts(nodePorts);
            applicationInstance.setService(applicationInstance.getName());
            String svcPorts = svc.getSpec().getPorts().stream().map(io.fabric8.kubernetes.api.model.ServicePort::getPort)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            applicationInstance.setServicePorts(svcPorts);
        } catch (ApiException e) {
            applicationInstance.setMessage(e.getMessage());
            throw new HotCloudException("Create application service exception: " + e.getMessage());
        }

        return null;
    }
}

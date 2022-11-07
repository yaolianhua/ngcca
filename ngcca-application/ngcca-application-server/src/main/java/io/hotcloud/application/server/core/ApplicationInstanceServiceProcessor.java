package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.Service;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.exception.HotCloudResourceConflictException;
import io.hotcloud.kubernetes.api.ServiceApi;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.network.DefaultServiceSpec;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
import io.hotcloud.kubernetes.model.network.ServiceSpec;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.common.api.CommonConstant.K8S_APP;

@Component
@RequiredArgsConstructor
class ApplicationInstanceServiceProcessor implements ApplicationInstanceProcessor <ApplicationInstance> {

    private final ServiceApi serviceApi;
    private final ApplicationInstanceService applicationInstanceService;

    @Override
    public int order() {
        return DEFAULT_ORDER + 2;
    }

    @Override
    public Type getType() {
        return Type.Ingress;
    }

    @SneakyThrows
    @Override
    public void processCreate(ApplicationInstance applicationInstance) {

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
                servicePort.setTargetPort(port);
                servicePorts.add(servicePort);
            }
            spec.setPorts(servicePorts);
            request.setServiceSpec(spec);

            Service fetched = serviceApi.read(metadata.getNamespace(), metadata.getName());
            if (Objects.nonNull(fetched)) {
                throw new HotCloudResourceConflictException("kubernetes service [" + metadata.getName() + "] has been existed in namespace [" + metadata.getNamespace() + "]");
            }
            Service svc = serviceApi.create(request);

            String nodePorts = svc.getSpec().getPorts().stream().map(io.fabric8.kubernetes.api.model.ServicePort::getNodePort)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            applicationInstance.setNodePorts(nodePorts);
            applicationInstance.setService(applicationInstance.getName());
            String svcPorts = svc.getSpec().getPorts().stream().map(io.fabric8.kubernetes.api.model.ServicePort::getPort)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            applicationInstance.setServicePorts(svcPorts);

            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.info(ApplicationInstanceServiceProcessor.class.getName(), String.format("[%s] user's application instance k8s service [%s] created", applicationInstance.getUser(), applicationInstance.getName()));
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(ApplicationInstanceServiceProcessor.class.getName(),
                    String.format("[%s] user's application instance k8s service [%s] created error: %s", applicationInstance.getUser(), applicationInstance.getName(), e.getMessage()));
            throw e;
        }

    }

    @SneakyThrows
    @Override
    public void processDelete(ApplicationInstance input) {
        Service service = serviceApi.read(input.getNamespace(), input.getName());
        if (Objects.nonNull(service)){
            serviceApi.delete(input.getNamespace(), input.getName());
            Log.info(ApplicationInstanceServiceProcessor.class.getName(), String.format("[%s] user's application instance k8s service [%s] deleted",input.getUser(), input.getName()));
        }
    }
}

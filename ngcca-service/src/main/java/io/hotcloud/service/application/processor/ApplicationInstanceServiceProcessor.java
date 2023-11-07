package io.hotcloud.service.application.processor;

import io.fabric8.kubernetes.api.model.Service;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.ResourceConflictException;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.network.DefaultServiceSpec;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
import io.hotcloud.kubernetes.model.network.ServiceSpec;
import io.hotcloud.service.application.ApplicationInstanceProcessor;
import io.hotcloud.service.application.ApplicationInstanceService;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.common.model.CommonConstant.K8S_APP;

@Component
@RequiredArgsConstructor
class ApplicationInstanceServiceProcessor implements ApplicationInstanceProcessor<ApplicationInstance> {

    private final ServiceClient serviceApi;
    private final ApplicationInstanceService applicationInstanceService;

    @Override
    public int order() {
        return DEFAULT_ORDER + 2;
    }

    @Override
    public Type getType() {
        return Type.SERVICE;
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

            final KubernetesCluster cluster = applicationInstance.getCluster();
            Service fetched = serviceApi.read(cluster.getAgentUrl(), metadata.getNamespace(), metadata.getName());
            if (Objects.nonNull(fetched)) {
                throw new ResourceConflictException("kubernetes service [" + metadata.getName() + "] has been existed in namespace [" + metadata.getNamespace() + "]");
            }
            Service svc = serviceApi.create(cluster.getAgentUrl(), request);

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
            Log.info(this, null, String.format("[%s] user's application instance k8s service [%s] created", applicationInstance.getUser(), applicationInstance.getName()));
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(this, null,
                    String.format("[%s] user's application instance k8s service [%s] created error: %s", applicationInstance.getUser(), applicationInstance.getName(), e.getMessage()));
            throw e;
        }

    }

    @SneakyThrows
    @Override
    public void processDelete(ApplicationInstance input) {
        final KubernetesCluster cluster = input.getCluster();
        Service service = serviceApi.read(cluster.getAgentUrl(), input.getNamespace(), input.getName());
        if (Objects.nonNull(service)) {
            serviceApi.delete(cluster.getAgentUrl(), input.getNamespace(), input.getName());
            Log.info(this, null, String.format("[%s] user's application instance k8s service [%s] deleted", input.getUser(), input.getName()));
        }
    }
}

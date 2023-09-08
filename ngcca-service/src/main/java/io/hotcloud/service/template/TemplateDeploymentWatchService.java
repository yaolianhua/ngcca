package io.hotcloud.service.template;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.ingress.IngressHelper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateDeploymentWatchService {
    private final TemplateInstanceService templateInstanceService;
    private final ApplicationProperties applicationProperties;
    private final DeploymentClient deploymentApi;
    private final ServiceClient serviceApi;
    private final KubectlClient kubectlApi;

    private final PodClient podApi;
    private final IngressHelper ingressHelper;

    public void watch(TemplateInstance template) {

        try {
            //if timeout
            int timeout = Instant.now().compareTo(template.getCreatedAt().plusSeconds(applicationProperties.getDeploymentTimeoutSecond()));
            if (timeout > 0) {
                String timeoutMessage = retrieveK8sEventsMessage(template);

                template.setMessage(timeoutMessage);
                template.setSuccess(false);
                template.setProgress(100);
                templateInstanceService.saveOrUpdate(template);

                Log.warn(this, null, String.format("[%s] user's template [%s] is failed! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));

                return;
            }

            //deploying
            Deployment deployment = deploymentApi.read(template.getNamespace(), template.getName());
            boolean ready = TemplateInstanceDeploymentStatus.isReady(deployment);
            if (!ready) {
                template.setProgress(50);
                templateInstanceService.saveOrUpdate(template);
                Log.info(this, null, String.format("[%s] user's template [%s] is not ready! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));
                return;
            }

            //deployment success
            String nodePorts;

            Service service = serviceApi.read(template.getNamespace(), template.getService());
            Assert.notNull(service, String.format("Read k8s service is null. namespace:%s, name:%s", template.getNamespace(), template.getName()));
            List<ServicePort> ports = service.getSpec().getPorts();
            if (!CollectionUtils.isEmpty(ports) && ports.size() > 1) {
                nodePorts = ports.stream()
                        .map(e -> String.valueOf(e.getNodePort()))
                        .collect(Collectors.joining(","));
            } else {
                nodePorts = String.valueOf(ports.get(0).getNodePort());
            }

            template.setNodePorts(nodePorts);
            template.setMessage(CommonConstant.SUCCESS_MESSAGE);
            template.setSuccess(true);
            template.setProgress(100);
            if (StringUtils.hasText(template.getIngress())) {
                template.setLoadBalancerIngressIp("Pending");
            }
            templateInstanceService.saveOrUpdate(template);

            if (StringUtils.hasText(template.getIngress())) {
                List<HasMetadata> metadataList = kubectlApi.resourceListCreateOrReplace(template.getNamespace(), YamlBody.of(template.getIngress()));
                String ingress = metadataList.stream()
                        .map(e -> e.getMetadata().getName())
                        .findFirst().orElse(null);

                String loadBalancerIngressIp = ingressHelper.getLoadBalancerIpString(template.getNamespace(), ingress);
                template.setLoadBalancerIngressIp(loadBalancerIngressIp);
                templateInstanceService.saveOrUpdate(template);

                Log.info(this, null, String.format("[%s] user's [%s] template ingress [%s] create success. loadBalanceIngressIp [%s]", template.getUser(), template.getName(), ingress, loadBalancerIngressIp));
            }

            Log.info(this, null, String.format("[%s] user's [%s] template [%s] deploy success.", template.getUser(), template.getName(), template.getId()));

        } catch (Exception e) {
            Log.error(this, null, io.hotcloud.common.log.Event.EXCEPTION, "template deployment watch error: " + e.getMessage());
            template.setSuccess(false);
            template.setMessage(e.getMessage());
            template.setProgress(100);
            templateInstanceService.saveOrUpdate(template);
        }
    }

    @NotNull
    private String retrieveK8sEventsMessage(TemplateInstance template) {
        String timeoutMessage = CommonConstant.TIMEOUT_MESSAGE;
        PodList podList = podApi.readList(template.getNamespace(), Map.of("app", template.getName()));
        if (Objects.nonNull(podList) && !CollectionUtils.isEmpty(podList.getItems())) {
            List<String> podNameList = podList.getItems()
                    .stream()
                    .map(e -> e.getMetadata().getName())
                    .collect(Collectors.toList());

            timeoutMessage = podNameList.stream()
                    .map(pod -> kubectlApi.namespacedPodEvents(template.getNamespace(), pod))
                    .flatMap(Collection::stream)
                    .filter(event -> Objects.equals("Warning", event.getType()))
                    .map(Event::getMessage)
                    .distinct()
                    .collect(Collectors.joining("\n"));
        }
        return timeoutMessage;
    }

}

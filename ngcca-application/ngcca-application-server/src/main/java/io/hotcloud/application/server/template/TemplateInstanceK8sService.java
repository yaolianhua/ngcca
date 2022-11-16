package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.template.TemplateDeploymentCacheApi;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateInstanceK8sService {
    private final TemplateInstanceService templateInstanceService;
    private final TemplateDeploymentCacheApi templateDeploymentCacheApi;
    private final DeploymentClient deploymentApi;
    private final ServiceClient serviceApi;
    private final KubectlClient kubectlApi;
    private final PodClient podApi;

    public void processTemplateCreateBlocked(TemplateInstance instance) {

        if (!templateDeploymentCacheApi.tryLock(instance.getId())) {
            return;
        }

        try {
            while (true) {
                TimeUnit.SECONDS.sleep(3);
                TemplateInstance template = templateInstanceService.findOne(instance.getId());
                //if deleted
                if (template == null) {
                    Log.warn(TemplateInstanceK8sService.class.getName(), String.format("[%s] user's [%s] template [%s] has been deleted", instance.getUser(), instance.getName(), instance.getId()));
                    templateDeploymentCacheApi.unLock(instance.getId());
                    return;
                }

                //if timeout
                int timeout = LocalDateTime.now().compareTo(template.getCreatedAt().plusSeconds(templateDeploymentCacheApi.getTimeoutSeconds()));
                if (timeout > 0) {
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

                    template.setMessage(timeoutMessage);
                    template.setSuccess(false);
                    templateInstanceService.saveOrUpdate(template);

                    Log.warn(TemplateInstanceK8sService.class.getName(), String.format("[%s] user's template [%s] is failed! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));

                    templateDeploymentCacheApi.unLock(instance.getId());
                    return;
                }

                //deploying
                Deployment deployment = deploymentApi.read(instance.getNamespace(), instance.getName());
                boolean ready = TemplateInstanceDeploymentStatus.isReady(deployment);
                if (!ready) {
                    Log.info(TemplateInstanceK8sService.class.getName(), String.format("[%s] user's template [%s] is not ready! deployment [%s] namespace [%s]", template.getUser(), template.getId(), template.getName(), template.getNamespace()));
                }

                //deployment success
                if (ready) {
                    String nodePorts;

                    Service service = serviceApi.read(template.getNamespace(), template.getService());
                    Assert.notNull(service, String.format("Read k8s service is null. namespace:%s, name:%s", template.getNamespace(), template.getName()));
                    List<ServicePort> ports = service.getSpec().getPorts();
                    if (!CollectionUtils.isEmpty(ports) && ports.size() > 1){
                        nodePorts = ports.stream()
                                .map(e -> String.valueOf(e.getNodePort()))
                                .collect(Collectors.joining(","));
                    }else {
                        nodePorts =  String.valueOf(ports.get(0).getNodePort());
                    }

                    template.setNodePorts(nodePorts);
                    template.setMessage(CommonConstant.SUCCESS_MESSAGE);
                    template.setSuccess(true);
                    templateInstanceService.saveOrUpdate(template);

                    Log.info(TemplateInstanceK8sService.class.getName(), String.format("[%s] user's [%s] template [%s] deploy success.", template.getUser(), template.getName(), template.getId()));

                    if (StringUtils.hasText(template.getIngress())) {
                        List<HasMetadata> metadataList = kubectlApi.resourceListCreateOrReplace(template.getNamespace(), YamlBody.of(template.getIngress()));
                        String ingress = metadataList.stream()
                                .map(e -> e.getMetadata().getName())
                                .findFirst().orElse(null);
                        Log.info(TemplateInstanceK8sService.class.getName(), String.format("[%s] user's [%s] template ingress [%s] create success.", template.getUser(), template.getName(), ingress));
                    }

                    templateDeploymentCacheApi.unLock(instance.getId());
                    return;
                }

            }

        } catch (Exception e) {
            templateDeploymentCacheApi.unLock(instance.getId());
            Log.error(TemplateInstanceK8sService.class.getName(), String.format("%s", e.getMessage()));
            instance.setSuccess(false);
            instance.setMessage(e.getMessage());
            templateInstanceService.saveOrUpdate(instance);
        }
    }

}

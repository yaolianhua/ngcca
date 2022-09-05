package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.application.api.template.event.*;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateEventListener {

    private final TemplateInstanceService templateInstanceService;
    private final ApplicationEventPublisher eventPublisher;
    private final DeploymentApi deploymentApi;
    private final ServiceApi serviceApi;
    private final KubectlApi kubectlApi;

    public InstanceTemplateEventListener(TemplateInstanceService templateInstanceService,
                                         ApplicationEventPublisher eventPublisher,
                                         DeploymentApi deploymentApi,
                                         ServiceApi serviceApi,
                                         KubectlApi kubectlApi) {
        this.templateInstanceService = templateInstanceService;
        this.eventPublisher = eventPublisher;
        this.deploymentApi = deploymentApi;
        this.serviceApi = serviceApi;
        this.kubectlApi = kubectlApi;
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @EventListener
    @Async
    public void timeout(TemplateInstanceTimeoutEvent event) {
        TemplateInstance template = event.getInstance();
        String namespace = template.getNamespace();
        try {
            String events = kubectlApi.events(namespace).stream()
                    .filter(e -> "warning".equalsIgnoreCase(e.getType()))
                    .map(Event::getMessage)
                    .distinct()
                    .collect(Collectors.joining("\n"));
            updateTemplate(template, events, false);
            Log.info(InstanceTemplateEventListener.class.getName(),
                    String.format("[%s] user's template [%s] is failed! deployment [%s] namespace [%s]",
                            template.getUser(), template.getId(), template.getName(), template.getNamespace()));
        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    String.format("%s", e.getMessage()));
            updateTemplate(template, e.getMessage(), false);
        }

    }

    @EventListener
    @Async
    public void started(TemplateInstanceStartedEvent event) {

        TemplateInstance instance = event.getInstance();
        String namespace = instance.getNamespace();
        String name = instance.getName();

        try {

            while (true) {

                sleep(10);
                TemplateInstance template = templateInstanceService.findOne(instance.getId());
                //if deleted
                if (template == null) {
                    Log.warn(InstanceTemplateEventListener.class.getName(),
                            TemplateInstanceStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's [%s] template [%s] has been deleted", instance.getUser(), instance.getName(), instance.getId()));
                    break;
                }

                //if timeout
                int timeout = LocalDateTime.now().compareTo(template.getCreatedAt().plusSeconds(600L));
                if (timeout > 0) {
                    eventPublisher.publishEvent(new TemplateInstanceTimeoutEvent(template));
                    break;
                }

                //deploying
                Deployment deployment = deploymentApi.read(namespace, name);
                boolean ready = TemplateInstanceDeploymentStatus.isReady(deployment);
                if (!ready) {
                    Log.info(InstanceTemplateEventListener.class.getName(),
                            TemplateInstanceStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's template [%s] is not ready! deployment [%s] namespace [%s]",
                                    template.getUser(), template.getId(), template.getName(), template.getNamespace()));
                }

                //deployment success
                if (ready) {
                    eventPublisher.publishEvent(new TemplateInstanceDoneEvent(template, true));
                    break;
                }

            }

        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceStartedEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
            updateTemplate(instance, e.getMessage(), false);
        }
    }

    @NotNull
    private String retrieveServiceNodePort(TemplateInstance template) {
        Service service = serviceApi.read(template.getNamespace(), template.getService());
        Assert.notNull(service, String.format("Read k8s service is null. namespace:%s, name:%s", template.getNamespace(), template.getName()));

        List<ServicePort> ports = service.getSpec().getPorts();
        if (!CollectionUtils.isEmpty(ports) && ports.size() > 1){
            return ports.stream()
                    .map(e -> String.valueOf(e.getNodePort()))
                    .collect(Collectors.joining(","));
        }
        return String.valueOf(ports.get(0).getNodePort());
    }

    @EventListener
    @Async
    public void startFailure(TemplateInstanceStartFailureEvent event) {
        TemplateInstance instance = event.getInstance();
        Throwable throwable = event.getThrowable();

        try {
            TemplateInstance updated = updateTemplate(instance, throwable.getMessage(), false);
            Log.info(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceStartFailureEvent.class.getSimpleName(),
                    String.format("instance template [%s] start failure. update instance template [%s]", updated.getName(), updated.getId()));
        } catch (Exception ex) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceStartFailureEvent.class.getSimpleName(),
                    String.format("%s", ex.getMessage()));
        }
    }

    @EventListener
    @Async
    public void delete(TemplateInstanceDeleteEvent event) {

        TemplateInstance instance = event.getInstance();
        String namespace = instance.getNamespace();
        String name = instance.getName();

        try {
            Boolean delete = kubectlApi.delete(namespace, instance.getYaml());
            Log.info(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceDeleteEvent.class.getSimpleName(),
                    String.format("Delete instance template k8s resource success [%s], namespace:%s, name:%s", delete, namespace, name));

            if (StringUtils.hasText(instance.getIngress())){
                Boolean deleteIngress = kubectlApi.delete(namespace, instance.getIngress());
                Log.info(InstanceTemplateEventListener.class.getName(),
                        TemplateInstanceDeleteEvent.class.getSimpleName(),
                        String.format("Delete instance template ingress success [%s], namespace:%s, name:%s", deleteIngress, namespace, name));
            }

        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceDeleteEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
        }
    }

    @EventListener
    @Async
    public void done(TemplateInstanceDoneEvent event) {

        TemplateInstance instance = event.getInstance();

        try {
            TemplateInstance template = templateInstanceService.findOne(instance.getId());
            if (template == null) {
                Log.warn(InstanceTemplateEventListener.class.getName(),
                        TemplateInstanceDoneEvent.class.getSimpleName(),
                        String.format("[%s] user's [%s] template [%s] has been deleted", instance.getUser(), instance.getName(), instance.getId()));
                return;
            }

            String nodePorts = retrieveServiceNodePort(instance);
            template.setNodePorts(nodePorts);

            TemplateInstance update = updateTemplate(template, "success", true);
            Log.info(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceDoneEvent.class.getSimpleName(),
                    String.format("[%s] user's [%s] template [%s] success. nodePorts [%s]", update.getUser(), update.getName(), update.getId(), update.getNodePorts()));

            if (StringUtils.hasText(template.getIngress())) {
                List<HasMetadata> metadataList = kubectlApi.apply(template.getNamespace(), template.getIngress());
                String ingress = metadataList.stream()
                        .map(e -> e.getMetadata().getName())
                        .findFirst().orElse(null);
                Log.info(InstanceTemplateEventListener.class.getName(),
                        TemplateInstanceDoneEvent.class.getSimpleName(),
                        String.format("[%s] user's [%s] template ingress [%s] create success.", update.getUser(), update.getName(), ingress));
            }

        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    TemplateInstanceDoneEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
        }

    }

    @NotNull
    private TemplateInstance updateTemplate(TemplateInstance template, String message, boolean success) {
        template.setMessage(message);
        template.setSuccess(success);
        Assert.hasText(template.getId(), "Instance template id is null");
        return templateInstanceService.saveOrUpdate(template);
    }
}

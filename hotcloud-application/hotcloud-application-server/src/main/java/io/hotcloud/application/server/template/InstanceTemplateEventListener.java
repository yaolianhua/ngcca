package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.event.*;
import io.hotcloud.kubernetes.api.configurations.ConfigMapApi;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimApi;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class InstanceTemplateEventListener {

    private final InstanceTemplateService instanceTemplateService;
    private final ApplicationEventPublisher eventPublisher;
    private final DeploymentApi deploymentApi;
    private final ServiceApi serviceApi;
    private final ConfigMapApi configMapApi;
    private final PersistentVolumeClaimApi persistentVolumeClaimApi;
    private final KubectlApi kubectlApi;

    public InstanceTemplateEventListener(InstanceTemplateService instanceTemplateService,
                                         ApplicationEventPublisher eventPublisher,
                                         DeploymentApi deploymentApi,
                                         ServiceApi serviceApi,
                                         ConfigMapApi configMapApi,
                                         KubectlApi kubectlApi,
                                         PersistentVolumeClaimApi persistentVolumeClaimApi) {
        this.instanceTemplateService = instanceTemplateService;
        this.eventPublisher = eventPublisher;
        this.deploymentApi = deploymentApi;
        this.serviceApi = serviceApi;
        this.configMapApi = configMapApi;
        this.kubectlApi = kubectlApi;
        this.persistentVolumeClaimApi = persistentVolumeClaimApi;
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
    public void timeout(InstanceTemplateTimeoutEvent event) {
        InstanceTemplate template = event.getInstance();
        String namespace = template.getNamespace();
        try {
            String events = kubectlApi.events(namespace).stream()
                    .filter(e -> "warning".equalsIgnoreCase(e.getType()))
                    .map(Event::getMessage)
                    .distinct()
                    .collect(Collectors.joining("\n"));
            updateTemplate(template, events, false);
            log.info("[{}] user's template [{}] is failed! deployment [{}] namespace [{}]",
                    template.getUser(), template.getId(), template.getName(), template.getNamespace());
        } catch (Exception e) {
            log.error("[InstanceTemplateTimeoutEvent] error {}", e.getMessage(), e);
            updateTemplate(template, e.getMessage(), false);
        }

    }

    @EventListener
    @Async
    public void started(InstanceTemplateStartedEvent event) {

        InstanceTemplate instance = event.getInstance();
        String namespace = instance.getNamespace();
        String name = instance.getName();

        try {

            while (true) {

                sleep(10);
                InstanceTemplate template = instanceTemplateService.findOne(instance.getId());
                //if deleted
                if (template == null) {
                    log.warn("[{}] user's template [{}] has been deleted", instance.getUser(), instance.getId());
                    break;
                }

                //if timeout
                int timeout = LocalDateTime.now().compareTo(template.getCreatedAt().plusSeconds(600L));
                if (timeout > 0) {
                    eventPublisher.publishEvent(new InstanceTemplateTimeoutEvent(template));
                    break;
                }

                //deploying
                Deployment deployment = deploymentApi.read(namespace, name);
                boolean ready = InstanceTemplateStatus.isReady(deployment);
                if (!ready) {
                    log.info("[{}] user's template [{}] is not ready! deployment [{}] namespace [{}]",
                            template.getUser(), template.getId(), template.getName(), template.getNamespace());
                }

                //deployment success
                if (ready) {
                    eventPublisher.publishEvent(new InstanceTemplateDoneEvent(template, true));
                    break;
                }

            }

        } catch (Exception e) {
            log.error("[InstanceTemplateStartedEvent] error {}", e.getMessage(), e);
            updateTemplate(instance, e.getMessage(), false);
        }
    }

    @NotNull
    private String retrieveServiceNodePort(InstanceTemplate template) {
        Service service;
        if (Objects.equals(template.getName(), Template.RedisInsight.name().toLowerCase())) {
            service = serviceApi.read(template.getNamespace(), String.format("%s-service", template.getName()));
        } else {
            service = serviceApi.read(template.getNamespace(), template.getName());
        }
        Assert.notNull(service, "k8s service is null");
        List<ServicePort> ports = service.getSpec().getPorts();
        if (Objects.equals(template.getName(), Template.Rabbitmq.name().toLowerCase())) {
            return ports.stream()
                    .map(e -> String.valueOf(e.getNodePort()))
                    .collect(Collectors.joining(","));
        }
        return String.valueOf(ports.get(0).getNodePort());
    }

    @EventListener
    @Async
    public void startFailure(InstanceTemplateStartFailureEvent event) {
        InstanceTemplate instance = event.getInstance();
        Throwable throwable = event.getThrowable();

        try {
            InstanceTemplate updated = updateTemplate(instance, throwable.getMessage(), false);
            log.info("[InstanceTemplateStartFailureEvent] update instance template [{}]", updated.getId());
        } catch (Exception ex) {
            log.error("[InstanceTemplateStartFailureEvent] error {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void delete(InstanceTemplateDeleteEvent event) {

        InstanceTemplate instance = event.getInstance();
        String namespace = instance.getNamespace();
        String name = instance.getName();

        try {
            Deployment deployment = deploymentApi.read(namespace, name);
            if (deployment != null) {
                deploymentApi.delete(namespace, name);
                log.info("[InstanceTemplateDeleteEvent] Delete deployment '{}'", name);
            }
            if (Objects.equals(name, Template.RedisInsight.name().toLowerCase())) {
                Service service = serviceApi.read(namespace, String.format("%s-service", name));
                if (service != null) {
                    serviceApi.delete(namespace, String.format("%s-service", name));
                    log.info("[InstanceTemplateDeleteEvent] Delete service '{}'", String.format("%s-service", name));
                }
            } else {
                Service service = serviceApi.read(namespace, name);
                if (service != null) {
                    serviceApi.delete(namespace, name);
                    log.info("[InstanceTemplateDeleteEvent] Delete service '{}'", name);
                }
            }


            String pvc = String.format("pvc-%s-%s", name, namespace);
            PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.read(namespace, pvc);
            if (persistentVolumeClaim != null) {
                persistentVolumeClaimApi.delete(pvc, namespace);
                log.info("[InstanceTemplateDeleteEvent] Delete persistentVolumeClaim '{}'", pvc);
            }

            ConfigMap configMap = configMapApi.read(namespace, name);
            if (configMap  != null){
                configMapApi.delete(namespace, name);
                log.info("[InstanceTemplateDeleteEvent] Delete configMap '{}'", name);
            }

        } catch (Exception e) {
            log.error("[InstanceTemplateDeleteEvent] error {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Async
    public void done(InstanceTemplateDoneEvent event) {

        InstanceTemplate instance = event.getInstance();

        try {
            InstanceTemplate template = instanceTemplateService.findOne(instance.getId());
            if (template == null) {
                log.warn("[{}] user's template [{}] has been deleted", instance.getUser(), instance.getId());
                return;
            }

            String nodePorts = retrieveServiceNodePort(instance);
            template.setNodePorts(nodePorts);

            InstanceTemplate update = updateTemplate(template, "success", true);
            log.info("[InstanceTemplateDoneEvent] update [{}] user's template [{}] success. nodePorts [{}]", update.getUser(), update.getId(), update.getNodePorts());
        } catch (Exception e) {
            log.error("[InstanceTemplateDoneEvent] error {}", e.getMessage(), e);
        }

    }

    @NotNull
    private InstanceTemplate updateTemplate(InstanceTemplate template, String message, boolean success) {
        template.setMessage(message);
        template.setSuccess(success);
        Assert.hasText(template.getId(), "Instance template id is null");
        return instanceTemplateService.saveOrUpdate(template);
    }
}

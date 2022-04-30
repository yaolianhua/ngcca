package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.event.*;
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

import java.time.LocalDateTime;
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
    private final PersistentVolumeClaimApi persistentVolumeClaimApi;
    private final KubectlApi kubectlApi;

    public InstanceTemplateEventListener(InstanceTemplateService instanceTemplateService,
                                         ApplicationEventPublisher eventPublisher,
                                         DeploymentApi deploymentApi,
                                         ServiceApi serviceApi,
                                         KubectlApi kubectlApi,
                                         PersistentVolumeClaimApi persistentVolumeClaimApi) {
        this.instanceTemplateService = instanceTemplateService;
        this.eventPublisher = eventPublisher;
        this.deploymentApi = deploymentApi;
        this.serviceApi = serviceApi;
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
                int timeout = LocalDateTime.now().compareTo(template.getCreatedAt().plusSeconds(300L));
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
            Service service = serviceApi.read(namespace, name);
            if (service != null) {
                serviceApi.delete(namespace, name);
                log.info("[InstanceTemplateDeleteEvent] Delete service '{}'", name);
            }

            String pvc = String.format("pvc-%s-%s", name, namespace);
            PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.read(namespace, pvc);
            if (persistentVolumeClaim != null) {
                persistentVolumeClaimApi.delete(pvc, namespace);
                log.info("[InstanceTemplateDeleteEvent] Delete persistentVolumeClaim '{}'", pvc);
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

            InstanceTemplate update = updateTemplate(template, "success", true);
            log.info("[InstanceTemplateDoneEvent] update [{}] user's template [{}] success", update.getUser(), update.getId());
        } catch (Exception e) {
            log.error("[InstanceTemplateDoneEvent] error {}", e.getMessage(), e);
        }

    }

    private InstanceTemplate updateTemplate(InstanceTemplate template, String message, boolean success) {
        template.setMessage(message);
        template.setSuccess(success);
        Assert.hasText(template.getId(), "Instance template id is null");
        return instanceTemplateService.saveOrUpdate(template);
    }
}

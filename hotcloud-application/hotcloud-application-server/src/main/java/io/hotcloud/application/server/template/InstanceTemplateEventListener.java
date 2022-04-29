package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.event.InstanceTemplateDeleteEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateDoneEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartFailureEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartedEvent;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class InstanceTemplateEventListener {

    private final InstanceTemplateService instanceTemplateService;
    private final ApplicationEventPublisher eventPublisher;
    private final DeploymentApi deploymentApi;

    public InstanceTemplateEventListener(InstanceTemplateService instanceTemplateService,
                                         ApplicationEventPublisher eventPublisher,
                                         DeploymentApi deploymentApi) {
        this.instanceTemplateService = instanceTemplateService;
        this.eventPublisher = eventPublisher;
        this.deploymentApi = deploymentApi;
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
    public void started(InstanceTemplateStartedEvent event) {

        InstanceTemplate instance = event.getInstance();
        String namespace = instance.getNamespace();
        String name = instance.getName();

        try {

            while (true) {

                sleep(10);
                InstanceTemplate template = instanceTemplateService.findOne(instance.getId());
                if (template == null) {
                    log.warn("[{}] user's template [{}] has been deleted", instance.getUser(), instance.getId());
                    break;
                }
                Deployment deployment = deploymentApi.read(namespace, name);
                boolean ready = InstanceTemplateStatus.isReady(deployment);
                if (!ready) {
                    log.info("[{}] user's template [{}] is not ready! deployment [{}] namespace [{}]",
                            template.getUser(), template.getId(), template.getName(), template.getNamespace());
                }

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
            String message = StringUtils.hasText(throwable.getMessage()) ? throwable.getMessage() : throwable.getCause().getMessage();

            InstanceTemplate updated = updateTemplate(instance, message, false);
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
            if (deployment == null) {
                return;
            }

            deploymentApi.delete(namespace, name);
            log.info("[InstanceTemplateDeleteEvent] Delete deployment '{}'", name);
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

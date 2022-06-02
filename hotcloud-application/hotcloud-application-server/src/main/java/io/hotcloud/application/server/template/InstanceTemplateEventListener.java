package io.hotcloud.application.server.template;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.event.*;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.configurations.ConfigMapApi;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeApi;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimApi;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
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
public class InstanceTemplateEventListener {

    private final InstanceTemplateService instanceTemplateService;
    private final ApplicationEventPublisher eventPublisher;
    private final DeploymentApi deploymentApi;
    private final ServiceApi serviceApi;
    private final ConfigMapApi configMapApi;
    private final PersistentVolumeClaimApi persistentVolumeClaimApi;
    private final PersistentVolumeApi persistentVolumeApi;
    private final KubectlApi kubectlApi;

    public InstanceTemplateEventListener(InstanceTemplateService instanceTemplateService,
                                         ApplicationEventPublisher eventPublisher,
                                         DeploymentApi deploymentApi,
                                         ServiceApi serviceApi,
                                         ConfigMapApi configMapApi,
                                         KubectlApi kubectlApi,
                                         PersistentVolumeClaimApi persistentVolumeClaimApi,
                                         PersistentVolumeApi persistentVolumeApi) {
        this.instanceTemplateService = instanceTemplateService;
        this.eventPublisher = eventPublisher;
        this.deploymentApi = deploymentApi;
        this.serviceApi = serviceApi;
        this.configMapApi = configMapApi;
        this.kubectlApi = kubectlApi;
        this.persistentVolumeClaimApi = persistentVolumeClaimApi;
        this.persistentVolumeApi = persistentVolumeApi;
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
                    Log.warn(InstanceTemplateEventListener.class.getName(),
                            InstanceTemplateStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's [%s] template [%s] has been deleted", instance.getUser(), instance.getName(), instance.getId()));
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
                boolean ready = InstanceTemplateDeploymentStatus.isReady(deployment);
                if (!ready) {
                    Log.info(InstanceTemplateEventListener.class.getName(),
                            InstanceTemplateStartedEvent.class.getSimpleName(),
                            String.format("[%s] user's template [%s] is not ready! deployment [%s] namespace [%s]",
                                    template.getUser(), template.getId(), template.getName(), template.getNamespace()));
                }

                //deployment success
                if (ready) {
                    eventPublisher.publishEvent(new InstanceTemplateDoneEvent(template, true));
                    break;
                }

            }

        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateStartedEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
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
            Log.info(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateStartFailureEvent.class.getSimpleName(),
                    String.format("instance template [%s] start failure. update instance template [%s]", updated.getName(), updated.getId()));
        } catch (Exception ex) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateStartFailureEvent.class.getSimpleName(),
                    String.format("%s", ex.getMessage()));
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
                Log.info(InstanceTemplateEventListener.class.getName(),
                        InstanceTemplateDeleteEvent.class.getSimpleName(),
                        String.format("Delete deployment '%s'", name));
            }
            if (Objects.equals(name, Template.RedisInsight.name().toLowerCase())) {
                Service service = serviceApi.read(namespace, String.format("%s-service", name));
                if (service != null) {
                    serviceApi.delete(namespace, String.format("%s-service", name));
                    Log.info(InstanceTemplateEventListener.class.getName(),
                            InstanceTemplateDeleteEvent.class.getSimpleName(),
                            String.format("Delete service '%s'", String.format("%s-service", name)));
                }
            } else {
                Service service = serviceApi.read(namespace, name);
                if (service != null) {
                    serviceApi.delete(namespace, name);
                    Log.info(InstanceTemplateEventListener.class.getName(),
                            InstanceTemplateDeleteEvent.class.getSimpleName(),
                            String.format("Delete service '%s'", name));
                }
            }


            String pvc = String.format("pvc-%s-%s", name, namespace);
            PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.read(namespace, pvc);
            if (persistentVolumeClaim != null) {
                persistentVolumeClaimApi.delete(pvc, namespace);
                Log.info(InstanceTemplateEventListener.class.getName(),
                        InstanceTemplateDeleteEvent.class.getSimpleName(),
                        String.format("Delete persistentVolumeClaim '%s'", pvc));
            }

            String pv = String.format("pv-%s-%s", name, namespace);
            PersistentVolume persistentVolume = persistentVolumeApi.read(pv);
            if (persistentVolume != null) {
                persistentVolumeApi.delete(pv);
                Log.info(InstanceTemplateEventListener.class.getName(),
                        InstanceTemplateDeleteEvent.class.getSimpleName(),
                        String.format("Delete persistentVolume '%s'", pv));
            }

            ConfigMap configMap = configMapApi.read(namespace, name);
            if (configMap != null) {
                configMapApi.delete(namespace, name);
                Log.info(InstanceTemplateEventListener.class.getName(),
                        InstanceTemplateDeleteEvent.class.getSimpleName(),
                        String.format("Delete configMap '%s'", name));
            }

        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateDeleteEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
        }
    }

    @EventListener
    @Async
    public void done(InstanceTemplateDoneEvent event) {

        InstanceTemplate instance = event.getInstance();

        try {
            InstanceTemplate template = instanceTemplateService.findOne(instance.getId());
            if (template == null) {
                Log.warn(InstanceTemplateEventListener.class.getName(),
                        InstanceTemplateDoneEvent.class.getSimpleName(),
                        String.format("[%s] user's [%s] template [%s] has been deleted", instance.getUser(), instance.getName(), instance.getId()));
                return;
            }

            String nodePorts = retrieveServiceNodePort(instance);
            template.setNodePorts(nodePorts);

            InstanceTemplate update = updateTemplate(template, "success", true);
            Log.info(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateDoneEvent.class.getSimpleName(),
                    String.format("[%s] user's [%s] template [%s] success. nodePorts [%s]", update.getUser(), update.getName(), update.getId(), update.getNodePorts()));
        } catch (Exception e) {
            Log.error(InstanceTemplateEventListener.class.getName(),
                    InstanceTemplateDoneEvent.class.getSimpleName(),
                    String.format("%s", e.getMessage()));
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

package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.Endpoint;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.event.InstanceTemplateDeleteEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartFailureEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartedEvent;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.server.cache.Cache;
import io.hotcloud.common.server.storage.FileHelper;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DefaultInstanceTemplatePlayer implements InstanceTemplatePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final ApplicationEventPublisher eventPublisher;
    private final InstanceTemplateService instanceTemplateService;
    private final InstanceTemplateActivityLogger activityLogger;
    private final KubectlApi kubectlApi;
    private final NamespaceApi namespaceApi;
    private final UserApi userApi;
    private final Cache cache;

    public DefaultInstanceTemplatePlayer(InstanceTemplateProcessors instanceTemplateProcessors,
                                         ApplicationEventPublisher eventPublisher,
                                         InstanceTemplateService instanceTemplateService,
                                         InstanceTemplateActivityLogger activityLogger,
                                         KubectlApi kubectlApi,
                                         NamespaceApi namespaceApi,
                                         UserApi userApi,
                                         Cache cache) {
        this.instanceTemplateProcessors = instanceTemplateProcessors;
        this.eventPublisher = eventPublisher;
        this.instanceTemplateService = instanceTemplateService;
        this.activityLogger = activityLogger;
        this.kubectlApi = kubectlApi;
        this.namespaceApi = namespaceApi;
        this.userApi = userApi;
        this.cache = cache;
    }

    @Override
    public InstanceTemplate play(Template template) {

        User current = userApi.current();
        Assert.notNull(current, "retrieve current user is null");
        //get user's namespace.
        String namespace = cache.get(String.format(SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");

        String yaml = instanceTemplateProcessors.process(template, namespace);
        Endpoint endpoint = this.retrieveEndpoint(template, namespace);
        String name = template.name().toLowerCase();
        InstanceTemplate instanceTemplate = InstanceTemplate.builder()
                .success(false)
                .name(name)
                .namespace(namespace)
                .user(current.getUsername())
                .service(endpoint.getHost())
                .ports(endpoint.getPorts())
                .yaml(yaml)
                .build();
        InstanceTemplate saved = instanceTemplateService.saveOrUpdate(instanceTemplate);
        log.info("[DefaultInstanceTemplatePlayer] Saved [{}] user's [{}] instance template [{}]", current.getUsername(), name, saved.getId());

        ActivityLog activityLog = activityLogger.log(ActivityAction.Create, saved);
        log.debug("[DefaultInstanceTemplatePlayer] activity [{}] saved", activityLog.getId());

        try {
            Path userPath = Path.of(ApplicationConstant.STORAGE_VOLUME_PATH, namespace, name);
            if (!FileHelper.exists(userPath)) {
                FileHelper.createDirectories(userPath);
            }
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.namespace(namespace);
            }
            kubectlApi.apply(namespace, yaml);
        } catch (Exception ex) {
            eventPublisher.publishEvent(new InstanceTemplateStartFailureEvent(saved, ex));
            return saved;
        }

        eventPublisher.publishEvent(new InstanceTemplateStartedEvent(saved));
        return saved;

    }

    @Override
    public void delete(String id) {
        Assert.hasText(id, "Instance template id is null");
        InstanceTemplate find = instanceTemplateService.findOne(id);
        Assert.notNull(find, "Can not found instance template [" + id + "]");

        instanceTemplateService.delete(id);
        log.info("[DefaultInstanceTemplatePlayer] Delete [{}] instance template '{}'", find.getName(), id);

        ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, find);
        log.debug("[DefaultInstanceTemplatePlayer] activity [{}] saved", activityLog.getId());

        eventPublisher.publishEvent(new InstanceTemplateDeleteEvent(find));
    }
}

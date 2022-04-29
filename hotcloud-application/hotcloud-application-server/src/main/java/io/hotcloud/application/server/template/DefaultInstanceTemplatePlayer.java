package io.hotcloud.application.server.template;

import io.hotcloud.application.api.Endpoint;
import io.hotcloud.application.api.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.event.InstanceTemplateDeleteEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartFailureEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartedEvent;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DefaultInstanceTemplatePlayer implements InstanceTemplatePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final ApplicationEventPublisher eventPublisher;
    private final InstanceTemplateService instanceTemplateService;
    private final KubectlApi kubectlApi;
    private final NamespaceApi namespaceApi;
    private final UserApi userApi;
    private final Cache cache;

    public DefaultInstanceTemplatePlayer(InstanceTemplateProcessors instanceTemplateProcessors,
                                         ApplicationEventPublisher eventPublisher,
                                         InstanceTemplateService instanceTemplateService,
                                         KubectlApi kubectlApi,
                                         NamespaceApi namespaceApi,
                                         UserApi userApi,
                                         Cache cache) {
        this.instanceTemplateProcessors = instanceTemplateProcessors;
        this.eventPublisher = eventPublisher;
        this.instanceTemplateService = instanceTemplateService;
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
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");

        String yaml = instanceTemplateProcessors.process(template, namespace);
        Endpoint endpoint = this.retrieveEndpoint(template, namespace);
        InstanceTemplate instanceTemplate = InstanceTemplate.builder()
                .success(false)
                .name(template.name().toLowerCase())
                .namespace(namespace)
                .user(current.getUsername())
                .service(endpoint.getHost())
                .port(endpoint.getPort())
                .yaml(yaml)
                .build();
        InstanceTemplate saved = instanceTemplateService.saveOrUpdate(instanceTemplate);
        log.info("[DefaultInstanceTemplatePlayer] Saved [{}] user's instance template [{}]", current.getUsername(), saved.getId());

        try {
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
        log.info("[DefaultInstanceTemplatePlayer] Delete instance template '{}'", id);

        eventPublisher.publishEvent(new InstanceTemplateDeleteEvent(find));
    }
}

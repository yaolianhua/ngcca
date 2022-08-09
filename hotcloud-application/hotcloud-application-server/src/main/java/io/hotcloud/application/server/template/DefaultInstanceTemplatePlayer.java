package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.Endpoint;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.event.InstanceTemplateDeleteEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartFailureEvent;
import io.hotcloud.application.api.template.event.InstanceTemplateStartedEvent;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@RequiredArgsConstructor
public class DefaultInstanceTemplatePlayer implements InstanceTemplatePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final ApplicationEventPublisher eventPublisher;
    private final InstanceTemplateService instanceTemplateService;
    private final InstanceTemplateActivityLogger activityLogger;
    private final KubectlApi kubectlApi;
    private final NamespaceApi namespaceApi;
    private final UserApi userApi;
    private final Cache cache;
    private final ApplicationProperties applicationProperties;

    @Override
    public InstanceTemplate play(Template template) {

        User current = userApi.current();
        Assert.notNull(current, "retrieve current user is null");
        //get user's namespace.
        String namespace = cache.get(String.format(SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");

        String yaml = instanceTemplateProcessors.process(template, namespace);
        String host = RandomStringUtils.randomAlphabetic(12).toLowerCase() + applicationProperties.getDotSuffixDomain();
        Endpoint endpoint = this.retrieveEndpoint(template, host);
        String name = template.name().toLowerCase();
        InstanceTemplate instanceTemplate = InstanceTemplate.builder()
                .success(false)
                .name(name)
                .namespace(namespace)
                .host(endpoint.getHost())
                .httpPort(endpoint.getHttpPort())
                .user(current.getUsername())
                .service(endpoint.getService())
                .ports(endpoint.getPorts())
                .yaml(yaml)
                .build();
        if (StringUtils.hasText(endpoint.getHost())){
            String ingressYaml = IngressTemplateRender.render(name, String.format("%s-%s", name, endpoint.getHost()),
                    endpoint.getHost(), "/",endpoint.getService(), endpoint.getHttpPort());
            instanceTemplate.setIngress(ingressYaml);
        }
        InstanceTemplate saved = instanceTemplateService.saveOrUpdate(instanceTemplate);
        Log.info(DefaultInstanceTemplatePlayer.class.getName(),
                String.format("Saved [%s] user's [%s] instance template [%s]", current.getUsername(), name, saved.getId()));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Create, saved);
        Log.debug(DefaultInstanceTemplatePlayer.class.getName(),
                String.format("Activity [%s] saved", activityLog.getId()));

        try {
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.create(namespace);
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
        Log.info(DefaultInstanceTemplatePlayer.class.getName(),
                String.format("Delete [%s] instance template '%s'", find.getName(), id));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, find);
        Log.debug(DefaultInstanceTemplatePlayer.class.getName(),
                String.format("Activity [%s] saved", activityLog.getId()));
        eventPublisher.publishEvent(new InstanceTemplateDeleteEvent(find));
    }
}

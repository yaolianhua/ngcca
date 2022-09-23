package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.application.api.template.event.TemplateInstanceDeleteEvent;
import io.hotcloud.application.api.template.event.TemplateInstanceStartFailureEvent;
import io.hotcloud.application.api.template.event.TemplateInstanceStartedEvent;
import io.hotcloud.application.server.template.processor.InstanceTemplateProcessors;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static io.hotcloud.common.api.CommonConstant.CK_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@RequiredArgsConstructor
public class DefaultTemplateInstancePlayer implements TemplateInstancePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final ApplicationEventPublisher eventPublisher;
    private final TemplateInstanceService templateInstanceService;
    private final InstanceTemplateActivityLogger activityLogger;
    private final KubectlApi kubectlApi;
    private final NamespaceApi namespaceApi;
    private final UserApi userApi;
    private final Cache cache;

    @Override
    public TemplateInstance play(Template template) {

        User current = userApi.current();
        Assert.notNull(current, "retrieve current user is null");
        //get user's namespace.
        String namespace = cache.get(String.format(CK_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");

        TemplateInstance templateInstance = instanceTemplateProcessors.process(template, current.getUsername(), namespace);

        TemplateInstance saved = templateInstanceService.saveOrUpdate(templateInstance);
        Log.info(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Saved [%s] user's [%s] instance template [%s]", current.getUsername(), templateInstance.getName(), saved.getId()));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Create, saved);
        Log.debug(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Activity [%s] saved", activityLog.getId()));

        try {
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.create(namespace);
            }
            kubectlApi.apply(namespace, templateInstance.getYaml());
        } catch (Exception ex) {
            eventPublisher.publishEvent(new TemplateInstanceStartFailureEvent(saved, ex));
            return saved;
        }

        eventPublisher.publishEvent(new TemplateInstanceStartedEvent(saved));
        return saved;

    }

    @Override
    public void delete(String id) {
        Assert.hasText(id, "Instance template id is null");
        TemplateInstance find = templateInstanceService.findOne(id);
        Assert.notNull(find, "Can not found instance template [" + id + "]");

        templateInstanceService.delete(id);
        Log.info(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Delete [%s] instance template '%s'", find.getName(), id));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, find);
        Log.debug(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Activity [%s] saved", activityLog.getId()));
        eventPublisher.publishEvent(new TemplateInstanceDeleteEvent(find));
    }
}

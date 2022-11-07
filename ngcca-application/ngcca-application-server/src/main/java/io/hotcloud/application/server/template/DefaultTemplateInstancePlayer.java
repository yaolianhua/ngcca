package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.application.api.template.event.TemplateInstanceStartedEvent;
import io.hotcloud.application.server.template.processor.InstanceTemplateProcessors;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@RequiredArgsConstructor
public class DefaultTemplateInstancePlayer implements TemplateInstancePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final ApplicationEventPublisher eventPublisher;
    private final TemplateInstanceService templateInstanceService;
    private final TemplateInstanceActivityLogger activityLogger;
    private final KubectlClient kubectlApi;
    private final NamespaceClient namespaceApi;
    private final UserApi userApi;
    private final TemplateInstanceK8sService templateInstanceK8sService;

    @Override
    public TemplateInstance play(Template template) {

        User current = userApi.current();
        Assert.notNull(current, "retrieve current user is null");
        String namespace = current.getNamespace();

        TemplateInstance templateInstance = instanceTemplateProcessors.process(template, current.getUsername(), namespace);

        TemplateInstance saved = templateInstanceService.saveOrUpdate(templateInstance);
        Log.info(DefaultTemplateInstancePlayer.class.getName(), String.format("Saved [%s] user's [%s] template [%s]", current.getUsername(), templateInstance.getName(), saved.getId()));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Create, saved);
        Log.debug(DefaultTemplateInstancePlayer.class.getName(), String.format("Activity [%s] saved", activityLog.getId()));

        try {
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.create(namespace);
            }
            kubectlApi.resourceListCreateOrReplace(namespace, YamlBody.of(templateInstance.getYaml()));
        } catch (Exception ex) {
            saved.setMessage(ex.getMessage());
            templateInstanceService.saveOrUpdate(saved);
            Log.error(DefaultTemplateInstancePlayer.class.getName(), String.format("template [%s] start failure.", saved.getName()));
            return saved;
        }

        eventPublisher.publishEvent(new TemplateInstanceStartedEvent(saved));
        return saved;

    }

    @Override
    public void delete(String id) {
        Assert.hasText(id, "Template instance id is null");
        TemplateInstance find = templateInstanceService.findOne(id);
        Assert.notNull(find, "Can not found template [" + id + "]");

        templateInstanceService.delete(id);
        Log.info(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Delete [%s] template '%s'", find.getName(), id));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, find);
        Log.debug(DefaultTemplateInstancePlayer.class.getName(),
                String.format("Activity [%s] saved", activityLog.getId()));

        templateInstanceK8sService.processTemplateDelete(find);
    }
}

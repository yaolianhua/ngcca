package io.hotcloud.server.application.template;

import io.hotcloud.common.model.ActivityAction;
import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.application.template.*;
import io.hotcloud.module.application.template.event.TemplateInstanceStartedEvent;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.server.application.template.processor.InstanceTemplateProcessors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
    private final TemplateDeploymentCacheApi templateDeploymentCacheApi;

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

        try {
            templateDeploymentCacheApi.unLock(find.getId());
            Boolean delete = kubectlApi.delete(find.getNamespace(), YamlBody.of(find.getYaml()));
            Log.info(DefaultTemplateInstancePlayer.class.getName(), String.format("Delete template k8s resource success [%s], namespace:%s, name:%s", delete, find.getNamespace(), find.getName()));

            if (StringUtils.hasText(find.getIngress())) {
                Boolean deleteIngress = kubectlApi.delete(find.getNamespace(), YamlBody.of(find.getIngress()));
                Log.info(DefaultTemplateInstancePlayer.class.getName(), String.format("Delete template ingress success [%s], namespace:%s, name:%s", deleteIngress, find.getNamespace(), find.getName()));
            }

        } catch (Exception e) {
            Log.error(DefaultTemplateInstancePlayer.class.getName(), String.format("%s", e.getMessage()));
        }
    }
}

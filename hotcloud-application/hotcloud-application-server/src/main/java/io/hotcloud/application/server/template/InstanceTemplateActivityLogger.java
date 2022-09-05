package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.api.activity.ActivityTarget;
import io.hotcloud.db.core.activity.ActivityEntity;
import io.hotcloud.db.core.activity.ActivityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateActivityLogger {

    private final ActivityRepository activityRepository;

    public InstanceTemplateActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, TemplateInstance template) {
        String description = "Unknown Activity";
        String namespace = template.getNamespace();
        String name = template.getName();
        if (Objects.equals(action, ActivityAction.Create) || Objects.equals(action, ActivityAction.Update)) {
            description = String.format("创建或更新命名空间【%s】下实例模板【%s】", namespace, name);
        }
        if (Objects.equals(action, ActivityAction.Delete)) {
            description = String.format("删除命名空间【%s】下实例模板【%s】", namespace, name);
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.Instance_Template.name())
                .targetId(template.getId())
                .targetName(name)
                .user(template.getUser())
                .description(description)
                .namespace(namespace)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        ActivityEntity entity = (ActivityEntity) new ActivityEntity().copyToEntity(activityLog);
        ActivityEntity saved = activityRepository.save(entity);

        return saved.toT(ActivityLog.class);
    }

}

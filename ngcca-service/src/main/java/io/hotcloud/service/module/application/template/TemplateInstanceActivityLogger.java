package io.hotcloud.service.module.application.template;

import io.hotcloud.common.model.activity.ALog;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.db.entity.ActivityEntity;
import io.hotcloud.module.db.entity.ActivityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class TemplateInstanceActivityLogger {

    private final ActivityRepository activityRepository;

    public TemplateInstanceActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ALog log(Action action, TemplateInstance template) {
        String description = "Unknown Activity";
        String namespace = template.getNamespace();
        String name = template.getName();
        if (Objects.equals(action, Action.CREATE) || Objects.equals(action, Action.UPDATE)) {
            description = String.format("创建或更新命名空间【%s】下实例模板【%s】", namespace, name);
        }
        if (Objects.equals(action, Action.DELETE)) {
            description = String.format("删除命名空间【%s】下实例模板【%s】", namespace, name);
        }
        ALog aLog = ALog.builder()
                .action(action.name())
                .target(Target.INSTANCE_TEMPLATE.name())
                .targetId(template.getId())
                .targetName(name)
                .user(template.getUser())
                .description(description)
                .namespace(namespace)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        ActivityEntity entity = (ActivityEntity) new ActivityEntity().toE(aLog);
        ActivityEntity saved = activityRepository.save(entity);

        return saved.toT(ALog.class);
    }

}

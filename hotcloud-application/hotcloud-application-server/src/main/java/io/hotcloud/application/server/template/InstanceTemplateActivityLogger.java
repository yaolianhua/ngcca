package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.common.activity.ActivityAction;
import io.hotcloud.common.activity.ActivityLog;
import io.hotcloud.common.activity.ActivityTarget;
import io.hotcloud.db.core.activity.ActivityEntity;
import io.hotcloud.db.core.activity.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class InstanceTemplateActivityLogger {

    private final ActivityRepository activityRepository;

    public InstanceTemplateActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, InstanceTemplate template) {
        String description = "Unknown Activity";
        String namespace = template.getNamespace();
        String name = template.getName();
        if (Objects.equals(action, ActivityAction.Create) || Objects.equals(action, ActivityAction.Update)) {
            description = String.format("Namespaced【%s】InstanceTemplate【%s】been created", namespace, name);
        }
        if (Objects.equals(action, ActivityAction.Delete)) {
            description = String.format("Namespaced【%s】InstanceTemplate【%s】been deleted", namespace, name);
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.BuildPack.name())
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

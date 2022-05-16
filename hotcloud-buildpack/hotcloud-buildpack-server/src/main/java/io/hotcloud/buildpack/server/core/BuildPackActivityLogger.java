package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPack;
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
public class BuildPackActivityLogger {

    private final ActivityRepository activityRepository;

    public BuildPackActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, BuildPack buildPack) {
        String description = "Unknown Activity";
        String namespace = buildPack.getJobResource().getNamespace();
        String name = buildPack.getJobResource().getName();
        if (Objects.equals(action, ActivityAction.Create) || Objects.equals(action, ActivityAction.Update)) {
            description = String.format("Namespaced【%s】BuildPack【%s】been created", namespace, name);
        }
        if (Objects.equals(action, ActivityAction.Delete)) {
            description = String.format("Namespaced【%s】BuildPack【%s】been deleted", namespace, name);
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.BuildPack.name())
                .targetId(buildPack.getId())
                .targetName(name)
                .user(buildPack.getUser())
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

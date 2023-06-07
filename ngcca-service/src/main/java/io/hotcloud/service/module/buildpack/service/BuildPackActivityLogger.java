package io.hotcloud.service.module.buildpack.service;

import io.hotcloud.common.model.activity.ActivityAction;
import io.hotcloud.common.model.activity.ActivityLog;
import io.hotcloud.common.model.activity.ActivityTarget;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.db.entity.ActivityEntity;
import io.hotcloud.module.db.entity.ActivityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class BuildPackActivityLogger {

    private final ActivityRepository activityRepository;

    public BuildPackActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, BuildPack buildPack) {
        String description = "Unknown Activity";
        String namespace = buildPack.getJobResource().getNamespace();
        String name = buildPack.getJobResource().getName();
        if (Objects.equals(action, ActivityAction.CREATE) || Objects.equals(action, ActivityAction.UPDATE)) {
            description = String.format("创建或更新命名空间【%s】下构建面板【%s】", namespace, name);
        }
        if (Objects.equals(action, ActivityAction.DELETE)) {
            description = String.format("删除命名空间【%s】下构建面板【%s】", namespace, name);
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.BUILDPACK.name())
                .targetId(buildPack.getId())
                .targetName(name)
                .user(buildPack.getUser())
                .description(description)
                .namespace(namespace)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        ActivityEntity entity = (ActivityEntity) new ActivityEntity().toE(activityLog);
        ActivityEntity saved = activityRepository.save(entity);

        return saved.toT(ActivityLog.class);
    }

}

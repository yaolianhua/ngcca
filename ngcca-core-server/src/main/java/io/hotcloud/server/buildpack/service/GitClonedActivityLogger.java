package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.ActivityAction;
import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.model.ActivityTarget;
import io.hotcloud.module.db.core.activity.ActivityEntity;
import io.hotcloud.module.db.core.activity.ActivityRepository;
import io.hotcloud.vendor.buildpack.GitCloned;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class GitClonedActivityLogger {

    private final ActivityRepository activityRepository;

    public GitClonedActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, GitCloned cloned) {
        String description = "Unknown Activity";
        if (Objects.equals(action, ActivityAction.Create) || Objects.equals(action, ActivityAction.Update)) {
            description = String.format("创建或更新Git仓库【%s】", cloned.getProject());
        }
        if (Objects.equals(action, ActivityAction.Delete)) {
            description = String.format("删除Git仓库【%s】", cloned.getProject());
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.Git_Clone.name())
                .targetId(cloned.getId())
                .targetName(cloned.getProject())
                .user(cloned.getUser())
                .description(description)
                .namespace("")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        ActivityEntity entity = (ActivityEntity) new ActivityEntity().toE(activityLog);
        ActivityEntity saved = activityRepository.save(entity);

        return saved.toT(ActivityLog.class);
    }

}

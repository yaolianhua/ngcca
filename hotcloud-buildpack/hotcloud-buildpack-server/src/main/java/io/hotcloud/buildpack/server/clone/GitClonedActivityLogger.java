package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.common.activity.ActivityAction;
import io.hotcloud.common.activity.ActivityLog;
import io.hotcloud.common.activity.ActivityTarget;
import io.hotcloud.db.core.activity.ActivityEntity;
import io.hotcloud.db.core.activity.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class GitClonedActivityLogger {

    private final ActivityRepository activityRepository;

    public GitClonedActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityLog log(ActivityAction action, GitCloned cloned) {
        String description = "Unknown Activity";
        if (Objects.equals(action, ActivityAction.Create) || Objects.equals(action, ActivityAction.Update)) {
            description = String.format("Git Repository【%s】been created", cloned.getProject());
        }
        if (Objects.equals(action, ActivityAction.Delete)) {
            description = String.format("Git Repository【%s】been deleted", cloned.getProject());
        }
        ActivityLog activityLog = ActivityLog.builder()
                .action(action.name())
                .target(ActivityTarget.Git_Clone.name())
                .targetId(cloned.getId())
                .targetName(cloned.getProject())
                .user(cloned.getUser())
                .description(description)
                .namespace("")
                .build();
        ActivityEntity entity = (ActivityEntity) new ActivityEntity().copyToEntity(activityLog);
        ActivityEntity saved = activityRepository.save(entity);

        return saved.toT(ActivityLog.class);
    }

}

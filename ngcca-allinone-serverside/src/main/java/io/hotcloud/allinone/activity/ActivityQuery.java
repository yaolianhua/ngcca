package io.hotcloud.allinone.activity;

import io.hotcloud.common.api.PageResult;
import io.hotcloud.common.api.Pageable;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.api.activity.ActivityTarget;
import io.hotcloud.db.core.activity.ActivityEntity;
import io.hotcloud.db.core.activity.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ActivityQuery {

    private final ActivityRepository activityRepository;

    public ActivityQuery(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Paging query activity log
     *
     * @param user     user's username
     * @param target   {@link ActivityTarget}
     * @param action   {@link ActivityAction}
     * @param pageable {@link Pageable}
     * @return activity log collection
     */
    public PageResult<ActivityLog> pagingQuery(String user, @Nullable ActivityTarget target, @Nullable ActivityAction action, Pageable pageable) {
        Assert.hasText(user, "user is null");

        List<ActivityEntity> entities = activityRepository.findByUser(user);
        List<ActivityLog> activityLogs = entities.stream().map(e -> e.toT(ActivityLog.class))
                .collect(Collectors.toList());

        List<ActivityLog> filtered = filter(activityLogs, target, action);
        //desc sorted
        filtered.sort((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()));

        return PageResult.ofPage(filtered, pageable.getPage(), pageable.getPageSize());

    }

    public List<ActivityLog> filter(List<ActivityLog> activities, ActivityTarget target, ActivityAction action) {
        if (null == target && action == null) {
            return activities;
        }
        if (target != null && action != null) {
            return activities.stream()
                    .filter(e -> target.name().equals(e.getTarget()))
                    .filter(e -> action.name().equals(e.getAction()))
                    .collect(Collectors.toList());
        }

        if (target != null) {
            return activities.stream()
                    .filter(e -> target.name().equals(e.getTarget()))
                    .collect(Collectors.toList());
        }

        return activities.stream()
                .filter(e -> action.name().equals(e.getAction()))
                .collect(Collectors.toList());
    }

}

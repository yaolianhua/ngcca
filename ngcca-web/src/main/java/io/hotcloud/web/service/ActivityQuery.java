package io.hotcloud.web.service;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.activity.ALog;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.module.db.entity.ActivityEntity;
import io.hotcloud.module.db.entity.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param target   {@link Target}
     * @param action   {@link Action}
     * @param pageable {@link Pageable}
     * @return activity log collection
     */
    public PageResult<ALog> pagingQuery(String user, @Nullable Target target, @Nullable Action action, Pageable pageable) {
        Assert.hasText(user, "user is null");

        List<ActivityEntity> entities = activityRepository.findByUser(user);
        List<ALog> aLogs = entities.stream()
                .map(e -> e.toT(ALog.class))
                .collect(Collectors.toList());

        List<ALog> filtered = filter(aLogs, target, action);
        //desc sorted
        filtered.sort((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()));

        return PageResult.ofCollectionPage(filtered, pageable);

    }

    public List<ALog> filter(List<ALog> activities, Target target, Action action) {
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

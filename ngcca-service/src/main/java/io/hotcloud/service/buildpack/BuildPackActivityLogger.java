package io.hotcloud.service.buildpack;

import io.hotcloud.common.model.activity.ALog;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.db.entity.ActivityEntity;
import io.hotcloud.module.db.entity.ActivityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class BuildPackActivityLogger {

    private final ActivityRepository activityRepository;

    public BuildPackActivityLogger(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ALog log(Action action, BuildPack buildPack) {

        String namespace = buildPack.getJobResource().getNamespace();
        String name = buildPack.getJobResource().getName();
        String description = String.format("[%s]-[%s]-[%s]-[%s]", action, namespace, Target.BUILDPACK, name);
        ALog aLog = ALog.builder()
                .action(action.name())
                .target(Target.BUILDPACK.name())
                .targetId(buildPack.getId())
                .targetName(name)
                .user(buildPack.getUser())
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

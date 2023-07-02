package io.hotcloud.web.mvc;

import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.module.db.entity.ActivityEntity;
import io.hotcloud.module.db.entity.ActivityRepository;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class GlobalLogAspect {
    private final UserApi userApi;
    private final ActivityRepository activityRepository;

    @Pointcut(value = "@annotation(io.hotcloud.web.mvc.Log)")
    public void cut() {
    }

    @AfterReturning(value = "cut()", returning = "result")
    private void log(JoinPoint point, Object result) {

        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return;
        }
        Log logAnnotation = ((MethodSignature) signature).getMethod().getAnnotation(Log.class);
        if (Objects.isNull(logAnnotation)) {
            return;
        }

        Action action = logAnnotation.action();
        Target target = logAnnotation.target();
        String activity = logAnnotation.activity();
        User user = userApi.current();


        ActivityEntity entity = new ActivityEntity();
        entity.setCreatedAt(LocalDateTime.now());
        entity.setAction(action == null ? "未知" : action.name());
        entity.setTarget(target == null ? "未知" : target.name());
        entity.setDescription(activity == null ? "未知" : activity);

        entity.setUser(user.getUsername());
        entity.setNamespace(user.getNamespace());

        activityRepository.save(entity);
    }

}

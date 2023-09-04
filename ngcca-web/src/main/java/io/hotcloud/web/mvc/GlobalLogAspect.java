package io.hotcloud.web.mvc;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.db.entity.ActivityEntity;
import io.hotcloud.db.entity.ActivityRepository;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
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

    @Around(value = "cut()")
    public Object log(ProceedingJoinPoint point) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = point.proceed(point.getArgs());
        long end = System.currentTimeMillis();
        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return result;
        }
        Method method = ((MethodSignature) signature).getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (Objects.isNull(logAnnotation)) {
            return result;
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();

        Action action = logAnnotation.action();
        Target target = logAnnotation.target();
        String activity = logAnnotation.activity();
        String username = "未知";

        if (request.getRequestURI().contains("/login")) {
            for (int i = 0; i < method.getParameters().length; i++) {
                if ("username".equalsIgnoreCase(method.getParameters()[i].getName())) {
                    username = ((String) point.getArgs()[i]);
                }
            }
        } else if (request.getRequestURI().contains("/logout")) {
            for (int i = 0; i < method.getParameters().length; i++) {
                if (method.getParameters()[i].isAnnotationPresent(CookieUser.class)) {
                    User cookieUser = (User) point.getArgs()[i];
                    username = cookieUser.getUsername();
                }
            }
        } else {
            User user = userApi.current();
            username = user.getUsername();
        }

        ActivityEntity entity = new ActivityEntity();
        entity.setCreatedAt(LocalDateTime.now());
        entity.setAction(action == null ? "未知" : action.name());
        entity.setTarget(target == null ? "未知" : target.name());
        entity.setDescription(activity == null ? "未知" : activity);
        entity.setExecuteMills((int) (end - start));
        entity.setMethod(request.getMethod());
        entity.setRequestIp(getClientIp(request));

        entity.setUser(username);

        activityRepository.save(entity);

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
        } catch (Exception e) {
            io.hotcloud.common.log.Log.error(this, e.getMessage(), Event.EXCEPTION, "get client ip error");
            return "unknown";
        }
    }

}

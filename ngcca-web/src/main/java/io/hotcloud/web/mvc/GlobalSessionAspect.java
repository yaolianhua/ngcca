package io.hotcloud.web.mvc;

import io.hotcloud.module.security.jwt.JwtVerifier;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Objects;

import static io.hotcloud.web.Views.*;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class GlobalSessionAspect {
    private final UserApi userApi;
    private final JwtVerifier jwtVerifier;

    @Pointcut(value = "@annotation(io.hotcloud.web.mvc.WebSession)")
    public void cut() {
    }

    @Around("cut()")
    private Object around(ProceedingJoinPoint point) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String authorization = WebCookie.retrieveCurrentHttpServletRequestAuthorization();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
        if (!StringUtils.hasText(authorization)) {
            if (request.getRequestURI().startsWith(PREFIX_ADMIN)) {
                return REDIRECT_ADMIN_LOGIN;
            }
            return REDIRECT_LOGIN;
        }

        if (!jwtVerifier.valid(authorization)) {
            if (request.getRequestURI().startsWith(PREFIX_ADMIN)) {
                return REDIRECT_ADMIN_LOGIN;
            }
            return REDIRECT_LOGIN;
        }


        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(authorization);
        String username = (String) attributes.get("username");
        User user = userApi.retrieve(username);

        if (!userApi.isAdmin(username) && (request.getRequestURI().startsWith(PREFIX_ADMIN))) {
            return REDIRECT_ADMIN_LOGIN;
        }

        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg.getClass().equals(BindingAwareModelMap.class)) {
                Model model = (BindingAwareModelMap) arg;
                model.addAttribute(WebConstant.USER, user);
                model.addAttribute(WebConstant.SERVER_ENDPOINT, "http://localhost:4000");
            }
        }

        return point.proceed(args);
    }
}

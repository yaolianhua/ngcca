package io.hotcloud.web.mvc;

import io.hotcloud.service.security.SecurityCookie;
import io.hotcloud.service.security.jwt.JwtVerifier;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.web.WebServerProperties;
import io.hotcloud.web.views.UserViews;
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

import static io.hotcloud.web.views.AdminViews.PREFIX_ADMIN;
import static io.hotcloud.web.views.AdminViews.REDIRECT_ADMIN_LOGIN;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class GlobalSessionAspect {
    private final UserApi userApi;
    private final JwtVerifier jwtVerifier;
    private final WebServerProperties webServerProperties;

    @Pointcut(value = "@annotation(io.hotcloud.web.mvc.WebSession)")
    public void cut() {
    }

    @Around("cut()")
    private Object around(ProceedingJoinPoint point) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String authorization = SecurityCookie.retrieveCurrentHttpServletRequestAuthorization();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
        if (!StringUtils.hasText(authorization)) {
            if (request.getRequestURI().startsWith(PREFIX_ADMIN)) {
                return REDIRECT_ADMIN_LOGIN;
            }
            return UserViews.REDIRECT_LOGIN;
        }

        if (!jwtVerifier.valid(authorization)) {
            if (request.getRequestURI().startsWith(PREFIX_ADMIN)) {
                return REDIRECT_ADMIN_LOGIN;
            }
            return UserViews.REDIRECT_LOGIN;
        }


        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(authorization);
        String username = (String) attributes.get("username");
        User user = userApi.retrieve(username);
        if (Objects.isNull(user)) {
            if (request.getRequestURI().startsWith(PREFIX_ADMIN)) {
                return REDIRECT_ADMIN_LOGIN;
            }
            return UserViews.REDIRECT_LOGIN;
        }

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
                model.addAttribute(WebConstant.SERVER_ENDPOINT, webServerProperties.getEndpoint());
            }
        }

        return point.proceed(args);
    }
}

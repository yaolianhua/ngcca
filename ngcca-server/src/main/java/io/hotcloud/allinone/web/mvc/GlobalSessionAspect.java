package io.hotcloud.allinone.web.mvc;

import io.hotcloud.security.api.jwt.JwtVerifier;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@Aspect
public class GlobalSessionAspect {
    private final UserApi userApi;
    private final JwtVerifier jwtVerifier;

    public GlobalSessionAspect(UserApi userApi, JwtVerifier jwtVerifier) {
        this.userApi = userApi;
        this.jwtVerifier = jwtVerifier;
    }

    @Pointcut(value = "@annotation(io.hotcloud.allinone.web.mvc.WebSession)")
    public void cut() {
    }

    @Around("cut()")
    private Object around(ProceedingJoinPoint point) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String authorization = WebCookie.retrieveCurrentHttpServletRequestAuthorization();
        if (!StringUtils.hasText(authorization)) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
            if (request.getRequestURI().startsWith("/administrator")) {
                return "redirect:/administrator/login";
            }
            return "redirect:/login";
        }

        if (!jwtVerifier.valid(authorization)) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
            if (request.getRequestURI().startsWith("/administrator")) {
                return "redirect:/administrator/login";
            }
            return "redirect:/login";
        }


        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(authorization);
        String username = (String) attributes.get("username");
        User user = userApi.retrieve(username);

        String[] parameterNames = ((CodeSignature) point.getSignature()).getParameterNames();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg.getClass().equals(BindingAwareModelMap.class)) {
                Model model = (BindingAwareModelMap) arg;
                model.addAttribute(WebConstant.USER, user);
                model.addAttribute(WebConstant.HOTCLOUD_ENDPOINT, "http://localhost:4000");
            }
        }

        return point.proceed(args);
    }
}

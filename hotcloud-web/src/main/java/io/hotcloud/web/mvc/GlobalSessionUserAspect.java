package io.hotcloud.web.mvc;

import io.hotcloud.web.login.LoginClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
@Aspect
public class GlobalSessionUserAspect {

    private final LoginClient loginClient;

    public GlobalSessionUserAspect(LoginClient loginClient) {
        this.loginClient = loginClient;
    }

    @Pointcut(value = "@annotation(io.hotcloud.web.mvc.WebUser)")
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

        User user = retrieve();
        String[] parameterNames = ((CodeSignature) point.getSignature()).getParameterNames();
        Object[] args = point.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }
            if (args[i].getClass().equals(User.class) && WebConstant.USER.equals(parameterNames[i])) {
                Arrays.fill(args, i, i + 1, user);
            }
            if (args[i].getClass().equals(BindingAwareModelMap.class)) {
                Model model = (BindingAwareModelMap) args[i];
                model.addAttribute(WebConstant.USER, user);
            }
        }

        return point.proceed(args);
    }

    private User retrieve() {
        ResponseEntity<Result<User>> response = loginClient.retrieveUser();
        if (response.getStatusCode().is2xxSuccessful()) {
            Result<User> body = response.getBody();
            Assert.notNull(body, "Response body is null");
            return body.getData();
        }

        return new User();
    }
}

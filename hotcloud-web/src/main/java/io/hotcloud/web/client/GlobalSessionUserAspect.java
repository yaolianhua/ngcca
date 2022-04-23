package io.hotcloud.web.client;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.client.login.LoginClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
    private final ClientAuthorizationManager authorizationManager;

    public GlobalSessionUserAspect(LoginClient loginClient,
                                   ClientAuthorizationManager authorizationManager) {
        this.loginClient = loginClient;
        this.authorizationManager = authorizationManager;
    }

    @Pointcut(value = "@annotation(SessionUser)")
    public void cut() {
    }

    @Around("cut()")
    private Object around(ProceedingJoinPoint point) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String authorization = authorizationManager.getAuthorization(Objects.requireNonNull(requestAttributes).getSessionId());
        if (!StringUtils.hasText(authorization)) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request.getRequestURI().startsWith("/administrator")) {
                return "redirect:/administrator/login";
            }
            return "redirect:/login";
        }

        String[] parameterNames = ((CodeSignature) point.getSignature()).getParameterNames();
        Object[] args = point.getArgs();
        for (int i = 0; i < args.length; i++) {
            if ("authorization".equalsIgnoreCase(parameterNames[i])) {
                Arrays.fill(args, i, i + 1, authorization);
            }

            if (args[i] == null) {
                continue;
            }
            if (args[i].getClass().equals(User.class)) {
                Arrays.fill(args, i, i + 1, retrieve());
            }
        }

        return point.proceed(args);
    }

    private User retrieve() {
        ResponseEntity<R<User>> response = loginClient.retrieveUser();
        if (response.getStatusCode().is2xxSuccessful()) {
            R<User> body = response.getBody();
            Assert.notNull(body, "Response body is null");
            return body.getData();
        }

        return new User();
    }
}

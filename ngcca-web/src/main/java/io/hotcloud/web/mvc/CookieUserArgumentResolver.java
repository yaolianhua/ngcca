package io.hotcloud.web.mvc;

import io.hotcloud.service.security.jwt.JwtVerifier;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CookieUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserApi userApi;
    private final JwtVerifier jwtVerifier;

    public CookieUserArgumentResolver(UserApi userApi, JwtVerifier jwtVerifier) {
        this.userApi = userApi;
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        CookieUser cookieUser = parameter.getParameterAnnotation(CookieUser.class);
        return Objects.nonNull(cookieUser) && User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String authorization = WebCookie.retrieveCurrentHttpServletRequestAuthorization();
        if (!jwtVerifier.valid(authorization)) {
            log.warn("Resolve web user failed. get authorization null from cookie");
            return null;
        }

        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(authorization);
        String username = (String) attributes.get("username");
        return userApi.retrieve(username);
    }
}

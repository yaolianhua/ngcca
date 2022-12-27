package io.hotcloud.allinone.web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public final class WebCookie {

    private WebCookie() {
    }

    public static Cookie generate(String authorization) {
        Cookie cookie = new Cookie(WebConstant.AUTHORIZATION, authorization);
        cookie.setPath("/");
        cookie.setComment("AUTHORIZATION");
        cookie.setHttpOnly(false);
        cookie.setVersion(1);
        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    public static String retrieveCurrentHttpServletRequestAuthorization() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (servletRequestAttributes == null) {
            log.warn("[WebCookie] servletRequestAttributes is null");
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("[WebCookie] cookie is null");
            return null;
        }
        Cookie cookie = Stream.of(cookies)
                .filter(e -> WebConstant.AUTHORIZATION.equals(e.getName()))
                .findFirst()
                .orElse(null);

        return cookie == null ? null : cookie.getValue();
    }

    public static boolean hasCurrentHttpServletRequestAuthorization() {
        return retrieveCurrentHttpServletRequestAuthorization() != null;
    }
}

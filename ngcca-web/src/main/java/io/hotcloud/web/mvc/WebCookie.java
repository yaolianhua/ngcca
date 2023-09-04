package io.hotcloud.web.mvc;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

@Slf4j
public final class WebCookie {

    private WebCookie() {
    }

    public static Cookie generateAuthorizationCookie(String authorization) {
        Cookie cookie = new Cookie(WebConstant.AUTHORIZATION, authorization);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    public static void removeAuthorizationCookie(HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = new Cookie(WebConstant.AUTHORIZATION, null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        request.getSession().invalidate();
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

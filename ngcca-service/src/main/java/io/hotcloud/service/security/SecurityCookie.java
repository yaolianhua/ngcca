package io.hotcloud.service.security;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

public final class SecurityCookie {

    private SecurityCookie() {
    }

    public static Cookie generateAuthorizationCookie(String authorization) {
        Cookie cookie = new Cookie(HttpRequestHeader.AUTHORIZATION, authorization);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    public static void removeAuthorizationCookie(HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = new Cookie(HttpRequestHeader.AUTHORIZATION, null);
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
            Log.warn(SecurityCookie.class.getName(), null, Event.NORMAL, "servletRequestAttributes is null");
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            Log.warn(SecurityCookie.class.getName(), null, Event.NORMAL, "get cookie from request is null");
            return null;
        }
        Cookie cookie = Stream.of(cookies)
                .filter(e -> HttpRequestHeader.AUTHORIZATION.equals(e.getName()))
                .findFirst()
                .orElse(null);

        return cookie == null ? null : cookie.getValue();
    }

    public static boolean hasCurrentHttpServletRequestAuthorization() {
        return retrieveCurrentHttpServletRequestAuthorization() != null;
    }
}

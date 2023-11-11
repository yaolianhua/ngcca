package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class Oauth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Log.warn(this, null, Event.EXCEPTION, "oauth2 authentication failed: " + exception.getMessage());
    }
}

package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Log;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public class GithubOauth2AuthenticationSuccessHandler {

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Log.info(this, null, "github authentication success");
    }
}

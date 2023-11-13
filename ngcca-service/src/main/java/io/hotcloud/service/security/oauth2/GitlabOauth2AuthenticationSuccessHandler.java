package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.service.security.SecurityCookie;
import io.hotcloud.service.security.login.BearerToken;
import io.hotcloud.service.security.login.LoginApi;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.Objects;

public class GitlabOauth2AuthenticationSuccessHandler {

    private final UserApi userApi;
    private final LoginApi loginApi;

    public GitlabOauth2AuthenticationSuccessHandler(UserApi userApi, LoginApi loginApi) {
        this.userApi = userApi;
        this.loginApi = loginApi;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();

        String username = Oauth2Helper.obtainGitlabUsernameOrGenerate(principal);
        User gitlabUser = Oauth2Helper.buildGitlabUser(principal);
        User databaseUser = userApi.retrieve(username);
        if (Objects.isNull(databaseUser)) {
            userApi.save(gitlabUser);
            Log.info(this, gitlabUser, Event.NORMAL, "[gitlab] social login. saved database user");
        }

        try {
            BearerToken bearerToken = loginApi.basicLogin(username, gitlabUser.getPassword());
            Cookie cookie = SecurityCookie.generateAuthorizationCookie(bearerToken.getAuthorization());
            response.addCookie(cookie);
            response.sendRedirect("/index");
            Log.info(this, gitlabUser, Event.NORMAL, "[gitlab] social login success");
        } catch (IOException e) {
            Log.error(this, gitlabUser, Event.EXCEPTION, "[gitlab] social login failure: " + e.getMessage());
        }
    }


}

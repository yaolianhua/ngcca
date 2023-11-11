package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.service.security.user.UserSocialSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.Objects;

public class GithubOauth2AuthenticationSuccessHandler {

    private final UserApi userApi;

    public GithubOauth2AuthenticationSuccessHandler(UserApi userApi) {
        this.userApi = userApi;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();

        String username = Oauth2Helper.obtainGithubUsernameOrGenerate(principal);
        User githubUser = Oauth2Helper.buildGithubUser(principal);
        User databaseUser = userApi.retrieve(username);
        if (Objects.isNull(databaseUser)) {
            userApi.save(githubUser);
            Log.info(this, githubUser, Event.NORMAL, "[github] social login. saved database user");
        }

        try {
            response.sendRedirect("/login?username=" + username + "&password=" + UserSocialSource.DEFAULT_PASSWORD);
            Log.info(this, githubUser, Event.NORMAL, "[github] social login redirect login");
        } catch (IOException e) {
            Log.error(this, githubUser, Event.EXCEPTION, "[github] social login redirect login failure");
        }
    }


}

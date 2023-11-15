package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.service.security.SecurityCookie;
import io.hotcloud.service.security.SecurityProperties;
import io.hotcloud.service.security.login.BearerToken;
import io.hotcloud.service.security.login.LoginApi;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.service.security.user.UserSocialSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Objects;

public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserApi userApi;
    private final LoginApi loginApi;

    public Oauth2AuthenticationSuccessHandler(UserApi userApi, LoginApi loginApi) {
        this.userApi = userApi;
        this.loginApi = loginApi;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();

        User user = null;
        if (Objects.equals(UserSocialSource.GITHUB, oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            user = Oauth2Helper.buildGithubUser(principal);
        }

        if (Objects.equals(UserSocialSource.GITLAB, oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            user = Oauth2Helper.buildGitlabUser(principal);
        }

        Assert.notNull(user, "oauth2 user is null");

        User databaseUser = userApi.retrieve(user.getUsername());
        if (Objects.isNull(databaseUser)) {
            userApi.save(user);
            Log.info(this, user, Event.NORMAL, "[" + user.getSocial() + "] social login. saved database user");
        }


        BearerToken bearerToken = loginApi.basicLogin(user.getUsername(), user.getPassword());
        Cookie cookie = SecurityCookie.generateAuthorizationCookie(bearerToken.getAuthorization());
        response.addCookie(cookie);
        response.sendRedirect(SecurityProperties.INDEX_PAGE);

    }
}

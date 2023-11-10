package io.hotcloud.service.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Objects;

public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GithubOauth2AuthenticationSuccessHandler githubOauth2AuthenticationSuccessHandler;

    public Oauth2AuthenticationSuccessHandler(GithubOauth2AuthenticationSuccessHandler githubOauth2AuthenticationSuccessHandler) {
        this.githubOauth2AuthenticationSuccessHandler = githubOauth2AuthenticationSuccessHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if (Objects.equals("github", oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            githubOauth2AuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }
}

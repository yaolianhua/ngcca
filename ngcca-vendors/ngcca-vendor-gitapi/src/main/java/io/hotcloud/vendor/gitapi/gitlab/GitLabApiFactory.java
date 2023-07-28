package io.hotcloud.vendor.gitapi.gitlab;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.gitapi.GitProxyProperties;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProxyClientConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GitLabApiFactory {

    private final GitProxyProperties properties;

    public GitLabApiFactory(GitProxyProperties properties) {
        this.properties = properties;
    }

    public static boolean isOfficialUrl(String url) {
        return url.startsWith("http://www.gitlab.com") ||
                url.startsWith("https://www.gitlab.com") ||
                url.startsWith("https://gitlab.com") ||
                url.startsWith("http://gitlab.com");
    }

    public static String resolvedUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new PlatformException("GitLab host url is null", 400);
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        return "http://" + url;
    }

    public GitLabApi create(String url, String username, String password) {
        url = resolvedUrl(url);
        Map<String, Object> proxyClientConfig = null;
        if (properties.hasProxy()) {
            proxyClientConfig = ProxyClientConfig.createProxyClientConfig(properties.getServer(), properties.getUsername(), properties.getPassword());
        }

        try {

            if (isOfficialUrl(url)) {
                return GitLabApi.oauth2Login(url, username, password, null, proxyClientConfig, true);
            }
            return GitLabApi.oauth2Login(url, username, password, true);

        } catch (GitLabApiException e) {
            throw new PlatformException(e.getMessage());
        }
    }

    public GitLabApi create(String url, String token) {
        url = resolvedUrl(url);
        Map<String, Object> proxyClientConfig = null;
        if (properties.hasProxy()) {
            proxyClientConfig = ProxyClientConfig.createProxyClientConfig(properties.getServer(), properties.getUsername(), properties.getPassword());
        }

        if (isOfficialUrl(url)) {
            return new GitLabApi(url, token, null, proxyClientConfig);
        }

        return new GitLabApi(url, token);

    }

}

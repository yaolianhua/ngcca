package io.hotcloud.vendor.gitapi;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProxyClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GitApiProperties.class)
public class GitApiConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GitLabApi gitLabApi(GitApiProperties properties) {
        Log.info(this, properties, Event.START, "load git api configuration");
        GitApiProperties.Gitlab gitlab = properties.getGitlab();
        Map<String, Object> proxyClientConfig = null;
        if (properties.hasProxy()) {
            proxyClientConfig = ProxyClientConfig.createProxyClientConfig(properties.getProxy().getServer(), properties.getProxy().getUsername(), properties.getProxy().getPassword());
        }

        if (gitlab.isBasicAuth()) {
            try {
                return GitLabApi.oauth2Login(gitlab.getUrl(), gitlab.getUsername(), gitlab.getPassword(), null, proxyClientConfig, true);
            } catch (GitLabApiException e) {
                throw new RuntimeException(e);
            }
        }

        return new GitLabApi(gitlab.getUrl(), gitlab.getAccessToken(), null, proxyClientConfig);
    }
}

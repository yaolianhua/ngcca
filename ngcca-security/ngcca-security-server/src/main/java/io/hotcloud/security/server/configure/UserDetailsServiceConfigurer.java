package io.hotcloud.security.server.configure;

import io.hotcloud.security.api.user.UserApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration(proxyBeanMethods = false)
public class UserDetailsServiceConfigurer {

    private final UserApi userApi;

    public UserDetailsServiceConfigurer(UserApi userApi) {
        this.userApi = userApi;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userApi::retrieve;
    }

}

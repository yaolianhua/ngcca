package io.hotcloud.security.admin.configure;

import io.hotcloud.security.api.UserApi;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author yaolianhua789@gmail.com
 **/
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

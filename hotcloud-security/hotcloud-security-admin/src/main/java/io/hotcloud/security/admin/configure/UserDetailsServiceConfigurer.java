package io.hotcloud.security.admin.configure;

import io.hotcloud.security.api.UserApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

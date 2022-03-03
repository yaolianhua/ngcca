package io.hotcloud.security.admin.configure;

import io.hotcloud.security.api.FakeUserApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author yaolianhua789@gmail.com
 **/
public class UserDetailsServiceConfigure {

    private final FakeUserApi fakeUserApi;

    public UserDetailsServiceConfigure(FakeUserApi fakeUserApi) {
        this.fakeUserApi = fakeUserApi;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return fakeUserApi::retrieve;
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

package io.hotcloud.security.autoconfigure;

import io.hotcloud.common.model.utils.Log;
import io.hotcloud.security.api.jwt.JwtVerifier;
import io.hotcloud.security.api.user.UserApi;
import io.hotcloud.security.autoconfigure.jwt.JwtAuthenticationFilter;
import io.hotcloud.security.autoconfigure.jwt.JwtConfiguration;
import io.hotcloud.security.autoconfigure.jwt.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        SecureWhitelistProperties.class,
        NgccaSecurityProperties.class,
        JwtProperties.class
})
@Import({
        JwtConfiguration.class,
})
public class NgccaSecurityAutoConfiguration {

    @Bean("noSecurityFilterChain")
    @ConditionalOnProperty(name = NgccaSecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "false")
    public SecurityFilterChain noSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.logout().disable();
        http.authorizeHttpRequests().anyRequest().permitAll();

        Log.warn(NgccaSecurityAutoConfiguration.class.getName(),
                "【Spring security disabled. if you want to enable, you need configure the environment 'security.enabled=true'】");
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = NgccaSecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecureWhitelistProperties whitelistProperties,
                                                   JwtVerifier jwtVerifier,
                                                   UserDetailsService userDetailsService) throws Exception {

        http.authorizeHttpRequests().requestMatchers(HttpMethod.OPTIONS).permitAll();
        //permit all whitelist
        http.authorizeHttpRequests().requestMatchers(whitelistProperties.getUrls().toArray(new String[0])).permitAll();

        http.cors();
        http.csrf().disable();
        http.sessionManagement().disable();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtVerifier, userDetailsService);
        //enable jwt auth
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //enable basic auth
        http.httpBasic().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        http.authorizeHttpRequests().anyRequest().authenticated();

        http.exceptionHandling().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        Log.info(NgccaSecurityAutoConfiguration.class.getName(), "【Spring security enabled. if you want to disable, you need configure the environment 'security.enabled=false'】");
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @ConditionalOnBean(UserApi.class)
    @Bean
    public UserDetailsService userDetailsService(UserApi userApi) {
        return userApi::retrieve;
    }
}

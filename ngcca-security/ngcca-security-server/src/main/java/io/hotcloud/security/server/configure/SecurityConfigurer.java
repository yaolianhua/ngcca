package io.hotcloud.security.server.configure;

import io.hotcloud.common.model.Log;
import io.hotcloud.security.api.jwt.JwtVerifier;
import io.hotcloud.security.server.Http401UnauthorizedEntryPoint;
import io.hotcloud.security.server.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Slf4j
public class SecurityConfigurer {

    private final SecureWhitelistProperties whitelistProperties;
    private final JwtVerifier jwtVerifier;
    private final UserDetailsService userDetailsService;

    public SecurityConfigurer(SecureWhitelistProperties whitelistProperties,
                              JwtVerifier jwtVerifier,
                              UserDetailsService userDetailsService) {
        this.whitelistProperties = whitelistProperties;
        this.jwtVerifier = jwtVerifier;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void print() {
        Log.info(SecurityConfigurer.class.getName(), "【Spring security enabled. if you want to disable, you need configure the environment 'security.enabled=false'】");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        //permit all whitelist
        http.authorizeRequests().antMatchers(whitelistProperties.getUrls().toArray(new String[0])).permitAll();

        http.cors();
        http.csrf().disable();
        http.sessionManagement().disable();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtVerifier, userDetailsService);
        //enable jwt auth
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //enable basic auth
        http.httpBasic().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        http.authorizeRequests().anyRequest().authenticated();

        http.exceptionHandling().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        return http.build();
    }

}

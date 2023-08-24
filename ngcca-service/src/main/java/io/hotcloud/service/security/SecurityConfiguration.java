package io.hotcloud.service.security;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.service.security.jwt.JwtAuthenticationFilter;
import io.hotcloud.service.security.jwt.JwtConfiguration;
import io.hotcloud.service.security.jwt.JwtProperties;
import io.hotcloud.service.security.jwt.JwtVerifier;
import io.hotcloud.service.security.user.UserApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        SecurityProperties.class,
        JwtProperties.class
})
@Import({
        JwtConfiguration.class,
})
public class SecurityConfiguration {

    @Bean("noSecurityFilterChain")
    @ConditionalOnProperty(name = SecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "false")
    public SecurityFilterChain noSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().permitAll());

        Log.warn(this, null, Event.START, "Spring security disabled. if you want to enable, you need configure the environment 'ngcca.security.enabled=true'");
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = SecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityProperties securityProperties,
                                                   JwtVerifier jwtVerifier,
                                                   UserDetailsService userDetailsService) throws Exception {

        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.requestMatchers(HttpMethod.OPTIONS).permitAll());
        //permit all whitelist
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.requestMatchers(securityProperties.getIgnoredUrls().toArray(new String[0])).permitAll());

        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(AbstractHttpConfigurer::disable);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtVerifier, userDetailsService);
        //enable jwt auth
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.securityContext(scc -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL));
        //enable basic auth
        http.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new Http401UnauthorizedEntryPoint()));

        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().authenticated());

        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new Http401UnauthorizedEntryPoint()));

        Log.info(this, null, Event.START, "Spring security enabled. if you want to disable, you need configure the environment 'ngcca.security.enabled=false'");
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

package io.hotcloud.security.server.configure;

import io.hotcloud.common.api.Log;
import io.hotcloud.security.server.Http401UnauthorizedEntryPoint;
import io.hotcloud.security.server.jwt.JwtAuthenticationFilter;
import io.hotcloud.security.server.jwt.JwtVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {
        SecureWhitelistProperties.class,
        SecurityProperties.class
})
@Import({
        CorsFilterConfigurer.class,
        JwtConfigurer.class,
        PasswordEncoderConfigurer.class,
        UserDetailsServiceConfigurer.class
})
@ConditionalOnProperty(name = SecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Slf4j
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final SecureWhitelistProperties whitelistProperties;
    private final JwtVerifier jwtVerifier;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfigurer(SecureWhitelistProperties whitelistProperties,
                              JwtVerifier jwtVerifier,
                              UserDetailsService userDetailsService,
                              PasswordEncoder passwordEncoder) {
        this.whitelistProperties = whitelistProperties;
        this.jwtVerifier = jwtVerifier;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void print() {
        Log.info(SecurityConfigurer.class.getName(), "【Spring security enabled. if you want to disable, you need configure the environment 'security.enabled=false'】");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

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
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String plainPassword = UUID.randomUUID().toString();
        //for test only
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("guest")
                .password(passwordEncoder.encode(plainPassword))
                .authorities(Collections.emptyList());
        log.info("***************************************************************************************************");
        log.info("* Generated random access password: user='guest', password='{}' *", plainPassword);
        log.info("***************************************************************************************************");
        //
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}

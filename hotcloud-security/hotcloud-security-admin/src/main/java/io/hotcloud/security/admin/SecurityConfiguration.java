package io.hotcloud.security.admin;

import io.hotcloud.security.admin.configure.CorsFilterConfigure;
import io.hotcloud.security.admin.configure.SecureWhitelistConfigure;
import io.hotcloud.security.admin.configure.UserDetailsServiceConfigure;
import io.hotcloud.security.admin.jwt.JwtAuthenticationFilter;
import io.hotcloud.security.admin.jwt.JwtVerifier;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecureWhitelistConfigure.class)
@Import({
        CorsFilterConfigure.class,
        UserDetailsServiceConfigure.class
})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecureWhitelistConfigure whitelistConfigure;
    private final JwtVerifier jwtVerifier;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(SecureWhitelistConfigure whitelistConfigure,
                                 JwtVerifier jwtVerifier,
                                 UserDetailsService userDetailsService,
                                 PasswordEncoder passwordEncoder) {
        this.whitelistConfigure = whitelistConfigure;
        this.jwtVerifier = jwtVerifier;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers(whitelistConfigure.getUrls().toArray(new String[0])).permitAll();

        http.cors();
        http.csrf().disable();
        http.sessionManagement().disable();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtVerifier, userDetailsService);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        http.authorizeRequests().anyRequest().authenticated();

        http.exceptionHandling().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}

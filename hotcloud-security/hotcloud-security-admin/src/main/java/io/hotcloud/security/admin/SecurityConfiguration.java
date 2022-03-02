package io.hotcloud.security.admin;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecureWhitelistConfigure.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecureWhitelistConfigure whitelistConfigure;

    public SecurityConfiguration(SecureWhitelistConfigure whitelistConfigure) {
        this.whitelistConfigure = whitelistConfigure;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers(whitelistConfigure.getUrls().toArray(new String[0])).permitAll();

        http.csrf().disable();
        http.sessionManagement().disable();

        http.httpBasic().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());

        http.authorizeRequests().anyRequest().authenticated();
    }


}

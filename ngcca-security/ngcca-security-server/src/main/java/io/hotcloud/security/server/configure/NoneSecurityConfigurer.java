package io.hotcloud.security.server.configure;

import io.hotcloud.common.model.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SecurityProperties.SECURITY_ENABLED_PROPERTY, havingValue = "false")
public class NoneSecurityConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().disable();
        http.logout().disable();
        http.authorizeRequests().anyRequest().permitAll();

        return http.build();
    }

    @PostConstruct
    public void print() {
        Log.warn(NoneSecurityConfigurer.class.getName(), "【Spring security disabled. if you want to enable, you need configure the environment 'security.enabled=true'】");
    }

}

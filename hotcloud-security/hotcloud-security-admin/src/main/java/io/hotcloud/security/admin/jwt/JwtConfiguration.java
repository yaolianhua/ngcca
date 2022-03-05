package io.hotcloud.security.admin.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {

    @Bean
    public JwtSigner jwtSigner(JwtProperties properties) {
        return new JwtManager(properties);
    }

    @Bean
    public JwtVerifier jwtVerifier(JwtProperties properties) {
        return new JwtManager(properties);
    }

}

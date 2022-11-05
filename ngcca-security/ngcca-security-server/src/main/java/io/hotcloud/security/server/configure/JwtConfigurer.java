package io.hotcloud.security.server.configure;

import io.hotcloud.security.server.jwt.JwtManager;
import io.hotcloud.security.server.jwt.JwtProperties;
import io.hotcloud.security.server.jwt.JwtSigner;
import io.hotcloud.security.server.jwt.JwtVerifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfigurer {

    @Bean
    public JwtSigner jwtSigner(JwtProperties properties) {
        return new JwtManager(properties);
    }

    @Bean
    public JwtVerifier jwtVerifier(JwtProperties properties) {
        return new JwtManager(properties);
    }

}

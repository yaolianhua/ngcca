package io.hotcloud.service.security.jwt;

import org.springframework.context.annotation.Bean;

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

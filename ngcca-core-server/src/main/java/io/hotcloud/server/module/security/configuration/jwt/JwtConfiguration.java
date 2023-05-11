package io.hotcloud.server.module.security.configuration.jwt;

import io.hotcloud.module.security.jwt.JwtSigner;
import io.hotcloud.module.security.jwt.JwtVerifier;
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

package io.hotcloud.security.autoconfigure.jwt;

import io.hotcloud.security.api.jwt.JwtSigner;
import io.hotcloud.security.api.jwt.JwtVerifier;
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

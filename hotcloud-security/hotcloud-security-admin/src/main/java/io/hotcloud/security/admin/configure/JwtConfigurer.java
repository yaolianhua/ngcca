package io.hotcloud.security.admin.configure;

import io.hotcloud.security.admin.jwt.JwtManager;
import io.hotcloud.security.admin.jwt.JwtProperties;
import io.hotcloud.security.admin.jwt.JwtSigner;
import io.hotcloud.security.admin.jwt.JwtVerifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
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

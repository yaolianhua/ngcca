package io.hotcloud.security.server.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@ConfigurationProperties("security.jwt")
@Data
public class JwtProperties {

    private String signKey = Jwt.SECRET;
    /**
     * JWA algorithm name for HMAC. default algorithm is {@code HS512}
     * <ul>
     *     <li> HS256
     *     <li> HS384
     *     <li> HS512
     * </ul>
     */
    private String algorithm = SignatureAlgorithm.HS512.getValue();

    @PostConstruct
    public void print() {
        log.info("【Load Jwt Properties】using JWA algorithm name '{}', sign-key '{}'", algorithm, signKey);
    }
}

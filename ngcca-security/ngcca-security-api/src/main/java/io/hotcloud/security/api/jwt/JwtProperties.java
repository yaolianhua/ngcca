package io.hotcloud.security.api.jwt;

import io.hotcloud.common.model.Log;
import io.hotcloud.common.model.Properties;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("security.jwt")
@Data
@Properties(prefix = "security.jwt")
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
        Log.info(JwtProperties.class.getName(), String.format("【Load Jwt Properties】using JWA algorithm name '%s', sign-key '%s'", algorithm, signKey));
    }
}

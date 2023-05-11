package io.hotcloud.server.module.security.configuration.jwt;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import io.hotcloud.module.security.jwt.Jwt;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security.jwt")
@Data
@Properties(prefix = CONFIG_PREFIX + "security.jwt")
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
        Log.info(this, this, Event.START, "load jwt properties");
    }
}

package io.hotcloud.security.admin.jwt;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JwtSigner {

    String sign(Jwt jwt);

    default String sign(Map<String, Object> attributes) {
        return sign(new JwtBody(attributes, null, null));
    }

    default String signExpiration(Map<String, Object> attributes, TimeUnit unit, int time) {
        return sign(new JwtBody(attributes, unit, time));
    }

}

package io.hotcloud.security.admin.jwt;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JwtSigner {

    String sign(Jwt jwt);

    default String sign(Map<String, Object> claims){
        return sign(new JwtBody(claims));
    }
}

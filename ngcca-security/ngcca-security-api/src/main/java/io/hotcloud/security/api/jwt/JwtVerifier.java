package io.hotcloud.security.api.jwt;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JwtVerifier {

    Jwt verify(String sign);

    default Map<String, Object> retrieveAttributes(String sign) {
        return verify(sign).payload().getAttributes();
    }

    default boolean valid(String sign) {
        try {
            verify(sign);
        } catch (Exception e) {
            //
            return false;
        }
        return true;
    }
}

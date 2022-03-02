package io.hotcloud.security.admin.jwt;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JwtVerifier {

    Jwt verify(String sign);
}

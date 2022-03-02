package io.hotcloud.security.admin.jwt;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JwtSigner {

    String sign(Jwt jwt);
}

package io.hotcloud.security.admin.jwt;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface Jwt {

    String SECRET = "!@#$%^&*(HOTCLOUD)!@#$%^&*(HOTCLOUD)!@#$%^&*(HOTCLOUD)";

    HeaderClaims header();

    PayloadClaims payload();

    String signKeySecret();

}

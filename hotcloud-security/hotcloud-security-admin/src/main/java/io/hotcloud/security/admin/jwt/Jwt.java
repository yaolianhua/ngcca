package io.hotcloud.security.admin.jwt;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface Jwt {

    HeaderClaims header();

    PayloadClaims payload();

    String signKeySecret();

}

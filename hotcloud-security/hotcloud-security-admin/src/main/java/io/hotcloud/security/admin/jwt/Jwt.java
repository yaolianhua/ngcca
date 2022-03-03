package io.hotcloud.security.admin.jwt;

import io.jsonwebtoken.Claims;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface Jwt {

    String SECRET = "!@#$%^&*(HOTCLOUD)!@#$%^&*(HOTCLOUD)!@#$%^&*(HOTCLOUD)";

    List<String> DEFAULT_CLAIMS = List.of(
            Claims.AUDIENCE,
            Claims.NOT_BEFORE,
            Claims.ID,
            Claims.EXPIRATION,
            Claims.ISSUED_AT,
            Claims.ISSUER,
            Claims.SUBJECT
    );

    HeaderClaims header();

    PayloadClaims payload();

}

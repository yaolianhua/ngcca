package io.hotcloud.security.admin.jwt;

import io.hotcloud.Assert;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class JwtManager implements JwtSigner, JwtVerifier {

    @Override
    public String sign(Jwt jwt) {

        HeaderClaims headerClaims = jwt.header();
        PayloadClaims payloadClaims = jwt.payload();

        JwtBuilder jwtBuilder = Jwts.builder();

        jwtBuilder.setHeader(headerClaims == null ? new HashMap<>(8) : headerClaims.ofMap());
        jwtBuilder.setClaims(payloadClaims == null ? new HashMap<>(16) : payloadClaims.ofMap());

        Assert.hasText(jwt.signKeySecret(), "Jwt sign key is null", 400);

        SecretKey secretKey = Keys.hmacShaKeyFor(jwt.signKeySecret().getBytes(StandardCharsets.UTF_8));
        jwtBuilder.signWith(secretKey, SignatureAlgorithm.HS512);

        return jwtBuilder.compact();
    }

    @Override
    public Jwt verify(String sign) {
        return null;
    }
}

package io.hotcloud.security.server.jwt;

import io.hotcloud.common.model.exception.HotCloudException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public class JwtManager implements JwtSigner, JwtVerifier {

    private final JwtProperties properties;

    public JwtManager(JwtProperties properties) {
        properties = properties == null ? new JwtProperties() : properties;
        this.properties = properties;
    }

    @Override
    public String sign(io.hotcloud.security.server.jwt.Jwt jwt) {

        HeaderClaims headerClaims = jwt.header();
        PayloadClaims payloadClaims = jwt.payload();

        JwtBuilder jwtBuilder = Jwts.builder();

        jwtBuilder.setHeader(headerClaims == null ? new HashMap<>(8) : headerClaims.ofMap());
        jwtBuilder.setClaims(payloadClaims == null ? new HashMap<>(16) : payloadClaims.ofMap());

        Assert.hasText(properties.getSignKey(), "Jwt sign key is null");

        byte[] encodedSecret = Base64.getEncoder().encode(properties.getSignKey().getBytes(StandardCharsets.UTF_8));
        SecretKey secretKey = Keys.hmacShaKeyFor(encodedSecret);
        jwtBuilder.signWith(secretKey, SignatureAlgorithm.valueOf(properties.getAlgorithm()));

        return jwtBuilder.compact();
    }

    @SuppressWarnings("unchecked")
    @Override
    public io.hotcloud.security.server.jwt.Jwt verify(String sign) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(io.hotcloud.security.server.jwt.Jwt.SECRET.getBytes(StandardCharsets.UTF_8)));
        io.jsonwebtoken.Jwt<JwsHeader<?>, Claims> jwt;
        try {
            jwt = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parse(sign);
        } catch (Exception e) {
            throw new HotCloudException(e.getMessage(), 401);
        }

        return new io.hotcloud.security.server.jwt.Jwt() {
            @Override
            public HeaderClaims header() {
                JwsHeader<?> header = jwt.getHeader();
                HeaderClaims headerClaims = new HeaderClaims();
                headerClaims.setAlgorithm(header.getAlgorithm());
                headerClaims.setContentType(header.getContentType());
                headerClaims.setType(header.getType());
                return headerClaims;
            }

            @Override
            public PayloadClaims payload() {
                Claims claims = jwt.getBody();
                PayloadClaims payloadClaims = new PayloadClaims();
                payloadClaims.setAudience(List.of(claims.getAudience()));
                payloadClaims.setId(claims.getId());
                payloadClaims.setIssuedAt(claims.getIssuedAt());
                payloadClaims.setSubject(claims.getSubject());
                payloadClaims.setIssuer(claims.getIssuer());
                payloadClaims.setExpiresAt(claims.getExpiration());
                payloadClaims.setNotBefore(claims.getNotBefore());

                io.hotcloud.security.server.jwt.Jwt.DEFAULT_CLAIMS.forEach(claims::remove);
                payloadClaims.setAttributes(claims);
                return payloadClaims;
            }

        };
    }
}

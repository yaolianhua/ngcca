package io.hotcloud.security.admin.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.Assert;
import io.hotcloud.HotCloudException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final List<String> DEFAULT_CLAIMS = List.of(
            Claims.AUDIENCE,
            Claims.NOT_BEFORE,
            Claims.ID,
            Claims.EXPIRATION,
            Claims.ISSUED_AT,
            Claims.ISSUER,
            Claims.SUBJECT
    );
    @SuppressWarnings("unchecked")
    @Override
    public Jwt verify(String sign) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(Jwt.SECRET.getBytes(StandardCharsets.UTF_8)));
        io.jsonwebtoken.Jwt<JwsHeader<?>,Claims> jwt;
        try {
             jwt = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parse(sign);
        }catch (Exception e){
            throw new HotCloudException(e.getMessage(), 401);
        }

        return new Jwt() {
            @Override
            public HeaderClaims header() {
                JwsHeader<?> header =  jwt.getHeader();
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

                DEFAULT_CLAIMS.forEach(claims::remove);
                payloadClaims.setAttributes(claims);
                return payloadClaims;
            }

            @Override
            public String signKeySecret() {
                return Base64.getEncoder().encodeToString(Jwt.SECRET.getBytes(StandardCharsets.UTF_8));
            }
        };
    }
}

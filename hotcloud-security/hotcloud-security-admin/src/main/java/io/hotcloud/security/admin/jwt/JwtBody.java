package io.hotcloud.security.admin.jwt;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public class JwtBody implements Jwt {

    private final Map<String, Object> data;

    public JwtBody(Map<String, Object> data) {
        data = Objects.isNull(data) ? new HashMap<>() : data;
        this.data = data;
    }

    @Override
    public HeaderClaims header() {
        HeaderClaims headerClaims = new HeaderClaims();
        headerClaims.setType("JWT");
        return headerClaims;
    }

    @Override
    public PayloadClaims payload() {
        PayloadClaims payloadClaims = new PayloadClaims();
        payloadClaims.setAttributes(data);
        payloadClaims.setAudience(List.of("Client", "Web"));
        payloadClaims.setIssuer("Hot Cloud");
        payloadClaims.setId(UUID.randomUUID().toString());
        payloadClaims.setIssuedAt(new Date());
        payloadClaims.setSubject("Api Auth");

        return payloadClaims;
    }

    @Override
    public String signKeySecret() {
        return Base64.getEncoder().encodeToString(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Object> getData() {
        return data;
    }
}

package io.hotcloud.security.server.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PayloadClaims {

    @JsonProperty("iss")
    private String issuer;
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("exp")
    private Date expiresAt;
    @JsonProperty("nbf")
    private Date notBefore;
    @JsonProperty("iat")
    private Date issuedAt;
    @JsonProperty("aud")
    private List<String> audience;
    @JsonProperty("jti")
    private String id;

    private Map<String, Object> attributes = new HashMap<>();

    public Map<String, Object> ofMap() {
        Map<String, Object> map = new HashMap<>(16);

        if (issuer != null) {
            map.put("iss", issuer);
        }
        if (subject != null) {
            map.put("sub", subject);
        }
        if (expiresAt != null) {
            map.put("exp", expiresAt);
        }
        if (notBefore != null) {
            map.put("nbf", notBefore);
        }
        if (issuedAt != null) {
            map.put("iat", issuedAt);
        }
        if (audience != null) {
            map.put("aud", audience);
        }
        if (id != null) {
            map.put("jti", id);
        }

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            map.putIfAbsent(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public void expiredAfterDays(Integer day) {
        expiredAfterHours(24 * day);
    }

    public void expiredAfterHours(Integer hour) {
        expiredAfterMinutes(60 * hour);
    }

    public void expiredAfterMinutes(Integer minute) {
        expiredAfterSeconds(60 * minute);
    }

    public void expiredAfterSeconds(Integer second) {
        issuedAt = new Date();
        notBefore = new Date();
        expiresAt = new Date(System.currentTimeMillis() + second * 1000);

    }


}

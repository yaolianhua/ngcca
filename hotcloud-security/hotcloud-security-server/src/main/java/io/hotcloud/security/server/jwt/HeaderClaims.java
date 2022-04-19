package io.hotcloud.security.server.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class HeaderClaims {

    @JsonProperty("alg")
    private String algorithm;
    @JsonProperty("typ")
    private String type;
    @JsonProperty("kid")
    private String keyId;
    @JsonProperty("cty")
    private String contentType;

    public Map<String, Object> ofMap() {
        Map<String, Object> map = new HashMap<>(8);

        if (algorithm != null) {
            map.put("alg", algorithm);
        }
        if (type != null) {
            map.put("typ", type);
        }
        if (keyId != null) {
            map.put("kid", keyId);
        }
        if (contentType != null) {
            map.put("cty", contentType);
        }

        return map;
    }
}

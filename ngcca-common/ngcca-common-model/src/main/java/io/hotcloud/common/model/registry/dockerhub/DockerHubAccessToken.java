package io.hotcloud.common.model.registry.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DockerHubAccessToken {

    private String token;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("issued_at")
    private LocalDateTime issuedAt;
}

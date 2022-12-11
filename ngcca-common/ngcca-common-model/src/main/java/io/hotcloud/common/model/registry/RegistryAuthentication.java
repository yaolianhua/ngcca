package io.hotcloud.common.model.registry;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class RegistryAuthentication {
    private String username;
    private String password;

    private String accessToken;

    public boolean isBasicAuth() {
        return StringUtils.hasText(username) && StringUtils.hasText(password);
    }

    public boolean isBearerAuth() {
        return StringUtils.hasText(accessToken);
    }
}

package io.hotcloud.vendor.gitapi.gitlab;

import lombok.Data;

@Data
public class GitLabRequestParameter {

    private String host;
    private String username;
    private String password;
    private String accessToken;

    public boolean isBasicAuth() {
        return username != null && !username.isBlank();
    }
}

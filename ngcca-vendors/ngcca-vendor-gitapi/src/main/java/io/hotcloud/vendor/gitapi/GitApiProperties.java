package io.hotcloud.vendor.gitapi;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "gitapi")
@Properties(prefix = CONFIG_PREFIX + "gitapi")
@Data
public class GitApiProperties {

    private Gitlab gitlab = new Gitlab();
    private ProxyServerConfig proxy;

    public boolean hasProxy() {
        return proxy != null && proxy.getServer() != null && !proxy.getServer().isBlank();
    }

    @Data
    public static class Gitlab {
        private String url = "https://www.gitlab.com";
        private String username;
        private String password;
        private String accessToken;

        public boolean isBasicAuth() {
            return this.username != null && !username.isBlank();
        }

    }


}

package io.hotcloud.server.module.application.openai;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "openai")
@Data
@Properties(prefix = CONFIG_PREFIX + "openai")
public class OpenAiProperties {

    private String apiKey;
    /**
     * Http proxy
     */
    private HttpProxy httpProxy;
    /**
     * http client timeout seconds
     */
    private int httpClientTimeoutSeconds = 60;

    @Data
    public static class HttpProxy {
        private String hostname;
        private int port;
    }

}

package io.hotcloud.application.server.chatgpt;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "chatgpt")
@Data
@Properties(prefix = CONFIG_PREFIX + "chatgpt")
public class ChatGPTProperties {

    private String apiKey;
    private String apiUrl = "https://api.openai.com";
}

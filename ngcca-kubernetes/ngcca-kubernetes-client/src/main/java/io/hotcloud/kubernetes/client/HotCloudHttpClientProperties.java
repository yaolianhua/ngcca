package io.hotcloud.kubernetes.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("ngcca.kubernetes-agent")
@Slf4j
@Data
public class HotCloudHttpClientProperties {

    private String host = "localhost";
    private Integer port = 1400;
    private String domainName;

    private String basicUsername = "admin";
    private String basicPassword = "e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea";

    @PostConstruct
    public void print() {
        log.info("【Ngcca kubernetes-agent address '{}', basic auth user '{}', basic password '{}'】", obtainUrl(), basicUsername, basicPassword);
    }

    public String obtainUrl() {
        if (StringUtils.hasText(domainName)) {
            return String.format("http://%s", domainName);
        }
        return String.format("http://%s:%s", host, port);
    }
}

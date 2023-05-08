package io.hotcloud.kubernetes.client.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("ngcca.kubernetes-agent")
@Slf4j
@Data
public class NgccaKubernetesAgentProperties {

    private String host = "localhost";
    private Integer port = 1400;
    private String domainName;

    @PostConstruct
    public void print() {
        log.info("load kubernetes agent address '{}', ", obtainUrl());
    }

    public String obtainUrl() {
        if (StringUtils.hasText(domainName)) {
            return String.format("http://%s", domainName);
        }
        return String.format("http://%s:%s", host, port);
    }
}

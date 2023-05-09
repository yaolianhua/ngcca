package io.hotcloud.kubernetes.client.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties("ngcca.kubernetes-agent")
@Slf4j
@Data
public class KubernetesAgentProperties {

    private String host = "localhost";
    private Integer port = 1400;
    private String domainName;

    @PostConstruct
    public void print() {
        log.info("load kubernetes agent address '{}'", getAgentHttpUrl());
    }

    public String getAgentHttpUrl() {
        if (StringUtils.hasText(domainName)) {
            return String.format("http://%s", domainName);
        }
        return String.format("http://%s:%s", host, port);
    }
}

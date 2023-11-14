package io.hotcloud.kubernetes.client.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties("ngcca.kubernetes-agent")
@Slf4j
@Data
public class KubernetesAgentProperties {

    private String endpoint;

    @PostConstruct
    public void print() {
        Assert.hasText(this.endpoint, "default kubernetes-agent endpoint is null");
        Assert.isTrue(this.endpoint.startsWith("http://") || this.endpoint.startsWith("https://"), "endpoint missing protocol");
        log.info("load default kubernetes-agent endpoint '{}'", getDefaultEndpoint());
    }

    public String getDefaultEndpoint() {
        return this.endpoint;
    }
}

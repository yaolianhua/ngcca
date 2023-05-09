package io.hotcloud.kubernetes.client.configuration;

import io.hotcloud.kubernetes.client.http.factory.KubernetesAgentClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(KubernetesAgentProperties.class)
@Import({
        KubernetesAgentConfiguration.class
})
public class KubernetesAgentAutoConfiguration {

    @Bean
    public KubernetesAgentClientFactory clientFactory(RestTemplate restTemplate,
                                                      KubernetesAgentProperties properties) {
        return new KubernetesAgentClientFactory(restTemplate, properties);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        builder.setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30));

        return builder.build();
    }
}

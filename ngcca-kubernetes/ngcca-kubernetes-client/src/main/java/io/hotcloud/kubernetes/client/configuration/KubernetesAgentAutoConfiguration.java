package io.hotcloud.kubernetes.client.configuration;

import io.hotcloud.kubernetes.client.http.factory.KubernetesAgentClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NgccaKubernetesAgentProperties.class)
@Import({
        RestTemplateConfiguration.class,
        KubernetesAgentConfiguration.class
})
public class KubernetesAgentAutoConfiguration {

    @Bean
    public KubernetesAgentClientFactory clientFactory(RestTemplate restTemplate,
                                                      NgccaKubernetesAgentProperties properties) {
        return new KubernetesAgentClientFactory(restTemplate, properties);
    }
}

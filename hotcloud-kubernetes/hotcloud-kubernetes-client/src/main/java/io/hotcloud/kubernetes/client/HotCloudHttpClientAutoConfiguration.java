package io.hotcloud.kubernetes.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HotCloudHttpClientProperties.class)
@Import({
        RestTemplateConfiguration.class,
        HotCloudHttpClientConfiguration.class
})
public class HotCloudHttpClientAutoConfiguration {

    @Bean
    public HotCloudHttpClientFactory clientFactory(RestTemplate restTemplate,
                                                   HotCloudHttpClientProperties properties) {
        return new HotCloudHttpClientFactory(restTemplate, properties);
    }
}

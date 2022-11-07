package io.hotcloud.kubernetes.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class RestTemplateConfiguration {

    private final NgccaKubernetesAgentProperties properties;

    public RestTemplateConfiguration(NgccaKubernetesAgentProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        builder.setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30));

        RestTemplate restTemplate = builder.build();

        restTemplate.getInterceptors().add(
                ((request, body, execution) -> {
                    log.info("HTTP '{}' Request To '{}'", Objects.requireNonNull(request.getMethod()).name(),
                            request.getURI());
                    return execution.execute(request, body);
                })
        );
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(properties.getBasicUsername(), properties.getBasicPassword()));

        return restTemplate;

    }

}

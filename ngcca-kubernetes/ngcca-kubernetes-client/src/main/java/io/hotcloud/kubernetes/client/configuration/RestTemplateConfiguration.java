package io.hotcloud.kubernetes.client.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
 class RestTemplateConfiguration {


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        builder.setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30));

        RestTemplate restTemplate = builder.build();

        restTemplate.getInterceptors().add(
                ((request, body, execution) -> {
                    log.debug("HTTP '{}' Request To '{}'", Objects.requireNonNull(request.getMethod()).name(),
                            request.getURI());
                    return execution.execute(request, body);
                })
        );

        return restTemplate;

    }

}

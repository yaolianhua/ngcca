package io.hotcloud.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableOpenApi
public class SwaggerConfiguration {

    @PostConstruct
    public void postProcess() {
        log.info("【Load Swagger Configuration】Enabled swagger api docs");
    }

}

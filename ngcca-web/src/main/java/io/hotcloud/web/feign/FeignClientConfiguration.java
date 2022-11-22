package io.hotcloud.web.feign;

import io.hotcloud.web.HotCloudWebApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@EnableFeignClients(basePackageClasses = HotCloudWebApplication.class)
@EnableHystrix
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HotCloudServerProperties.class)
@Import(BearerTokenRequestInterceptor.class)
@Slf4j
public class FeignClientConfiguration {

    private final HotCloudServerProperties properties;

    public FeignClientConfiguration(HotCloudServerProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        log.info("【Load feign-client Properties】server endpoint '{}'", String.format("http://%s:%s", properties.getHost(), properties.getPort()));
    }


}

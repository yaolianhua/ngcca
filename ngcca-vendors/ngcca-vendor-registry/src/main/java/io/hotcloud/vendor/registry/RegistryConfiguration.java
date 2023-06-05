package io.hotcloud.vendor.registry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DockerProperties.class)
public class RegistryConfiguration {

}

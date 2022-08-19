package io.hotcloud.common.server.registry;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.registry.RegistryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = RegistryProperties.class)
public class RegistryConfiguration {

    private final RegistryProperties registryProperties;

    public RegistryConfiguration(RegistryProperties registryProperties) {
        this.registryProperties = registryProperties;
    }

    @PostConstruct
    public void print(){
        Assert.notNull(registryProperties, "Registry properties is null");
        Assert.hasText(registryProperties.getUrl(), "Registry url is null");
        Log.info(RegistryConfiguration.class.getName(), String.format("【Load Registry Configuration. registry='%s'】", registryProperties.getUrl()));
    }
}

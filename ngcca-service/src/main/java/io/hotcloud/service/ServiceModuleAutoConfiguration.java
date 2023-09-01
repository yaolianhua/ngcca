package io.hotcloud.service;

import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.buildpack.BuildPackProperties;
import io.hotcloud.service.git.GitProxyProperties;
import io.hotcloud.service.openai.OpenAiConfiguration;
import io.hotcloud.service.registry.SystemRegistryImageProperties;
import io.hotcloud.service.registry.SystemRegistryProperties;
import io.hotcloud.service.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties({
        SystemRegistryImageProperties.class,
        SystemRegistryProperties.class,
        GitProxyProperties.class,
        ApplicationProperties.class,
        BuildPackProperties.class
})
@Import({
        OpenAiConfiguration.class,
        SecurityConfiguration.class
})
public class ServiceModuleAutoConfiguration {

}

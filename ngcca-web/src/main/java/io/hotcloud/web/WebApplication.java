package io.hotcloud.web;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.module.db.MongodbConfiguration;
import io.hotcloud.service.NgccaServiceModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MongodbConfiguration.class,
        NgccaServiceModule.class,
        WebApplication.class
})
@EnableKubernetesAgentClient
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}

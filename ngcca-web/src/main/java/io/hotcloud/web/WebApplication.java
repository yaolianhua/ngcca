package io.hotcloud.web;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.service.NgccaServiceModule;
import io.hotcloud.vendor.minio.MinioConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MinioConfiguration.class,
        NgccaServiceModule.class,
        WebApplication.class
})
@EnableKubernetesAgentClient
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}

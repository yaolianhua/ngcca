package io.hotcloud.service;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.module.db.MongodbConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MongodbConfiguration.class,
        NgccaCoreServerApplication.class
})
@EnableKubernetesAgentClient
public class NgccaCoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaCoreServerApplication.class, args);
    }
}

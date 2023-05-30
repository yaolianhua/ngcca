package io.hotcloud.server;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.module.db.MongodbConfiguration;
import io.hotcloud.service.NgccaServiceModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MongodbConfiguration.class,
        NgccaServiceModule.class
})
@EnableKubernetesAgentClient
public class NgccaCoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaCoreServerApplication.class, args);
    }
}

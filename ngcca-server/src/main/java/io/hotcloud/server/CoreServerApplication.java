package io.hotcloud.server;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.module.db.MongodbConfiguration;
import io.hotcloud.service.NgccaServiceModule;
import io.hotcloud.vendor.minio.MinioConfiguration;
import io.hotcloud.vendor.registry.RegistryConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MongodbConfiguration.class,
        MinioConfiguration.class,
        RegistryConfiguration.class,
        NgccaServiceModule.class,
        CoreServerApplication.class
})
@EnableKubernetesAgentClient
public class CoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreServerApplication.class, args);
    }
}

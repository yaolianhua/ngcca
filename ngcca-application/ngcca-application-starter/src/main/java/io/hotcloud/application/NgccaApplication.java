package io.hotcloud.application;

import io.hotcloud.buildpack.NgccaBuildPackRootPackage;
import io.hotcloud.common.NgccaCommonRootPackage;
import io.hotcloud.db.NgccaMongodbConfiguration;
import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.security.NgccaSecurityRootPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        NgccaBuildPackRootPackage.class,
        NgccaCommonRootPackage.class,
        NgccaSecurityRootPackage.class,
        NgccaMongodbConfiguration.class,
        NgccaApplication.class
})
@EnableKubernetesAgentClient
public class NgccaApplication {
    public static void main(String[] args) {
        SpringApplication.run(NgccaApplication.class, args);
    }
}

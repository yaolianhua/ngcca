package io.hotcloud.buildpack;

import io.hotcloud.common.NgccaCommonRootPackage;
import io.hotcloud.db.NgccaMongodbConfiguration;
import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.security.NgccaSecurityRootPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        NgccaMongodbConfiguration.class,
        NgccaSecurityRootPackage.class,
        NgccaCommonRootPackage.class,
        NgccaBuildPackRootPackage.class
})
@EnableKubernetesAgentClient
public class NgccaBuildPackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaBuildPackApplication.class, args);
    }
}

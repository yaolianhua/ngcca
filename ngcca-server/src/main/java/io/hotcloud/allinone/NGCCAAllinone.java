package io.hotcloud.allinone;

import io.hotcloud.application.NgccaApplicationRootPackage;
import io.hotcloud.buildpack.NgccaBuildPackRootPackage;
import io.hotcloud.common.NgccaCommonRootPackage;
import io.hotcloud.db.NgccaMongodbConfiguration;
import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.security.NgccaSecurityRootPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        NgccaApplicationRootPackage.class,
        NgccaBuildPackRootPackage.class,
        NgccaSecurityRootPackage.class,
        NgccaCommonRootPackage.class,
        NgccaMongodbConfiguration.class
})
@EnableKubernetesAgentClient
public class NGCCAAllinone {

    public static void main(String[] args) {
        SpringApplication.run(NGCCAAllinone.class, args);
    }
}

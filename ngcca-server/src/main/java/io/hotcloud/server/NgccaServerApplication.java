package io.hotcloud.server;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import io.hotcloud.vendor.gitapi.GitProxyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        GitProxyProperties.class,
        NgccaServerApplication.class
})
@EnableKubernetesAgentClient
public class NgccaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaServerApplication.class, args);
    }
}

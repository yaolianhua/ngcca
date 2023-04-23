package io.hotcloud.server;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.hotcloud.common", "io.hotcloud.server"})
@EnableKubernetesAgentClient
public class NgccaCoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaCoreServerApplication.class, args);
    }
}

package io.hotcloud.server;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKubernetesAgentClient
public class NgccaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NgccaServerApplication.class, args);
    }
}

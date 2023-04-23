package io.hotcloud.allinone;

import io.hotcloud.kubernetes.client.EnableKubernetesAgentClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKubernetesAgentClient
public class NGCCAAllinone {

    public static void main(String[] args) {
        SpringApplication.run(NGCCAAllinone.class, args);
    }
}

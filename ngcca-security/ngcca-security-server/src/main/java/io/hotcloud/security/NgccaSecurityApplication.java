package io.hotcloud.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.hotcloud.db", "io.hotcloud.common", "io.hotcloud.security"})
public class NgccaSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(NgccaSecurityApplication.class, args);
    }
}

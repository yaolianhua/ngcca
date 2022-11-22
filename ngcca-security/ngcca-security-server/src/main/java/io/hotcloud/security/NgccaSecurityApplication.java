package io.hotcloud.security;

import io.hotcloud.db.NgccaMongodbConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackageClasses = {NgccaMongodbConfiguration.class, NgccaSecurityApplication.class}
)
public class NgccaSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(NgccaSecurityApplication.class, args);
    }
}

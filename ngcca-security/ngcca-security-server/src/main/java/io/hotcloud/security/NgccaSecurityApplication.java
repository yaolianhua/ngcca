package io.hotcloud.security;

import io.hotcloud.db.MongodbConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackageClasses = {MongodbConfiguration.class, NgccaSecurityApplication.class}
)
public class NgccaSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(NgccaSecurityApplication.class, args);
    }
}

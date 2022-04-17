package io.hotcloud.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HotCloudWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotCloudWebApplication.class, args);
    }
}

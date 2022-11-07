package io.hotcloud.allinone;

import io.hotcloud.kubernetes.client.EnableHotCloudHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackages = {
                "io.hotcloud.db",
                "io.hotcloud.common",
                "io.hotcloud.buildpack",
                "io.hotcloud.security",
                "io.hotcloud.application",
                "io.hotcloud.allinone"
        }
)
@EnableHotCloudHttpClient
public class HotCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotCloudApplication.class, args);
    }
}

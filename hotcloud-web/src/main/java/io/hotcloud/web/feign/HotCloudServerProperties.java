package io.hotcloud.web.feign;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Slf4j
@ConfigurationProperties(prefix = "hotcloud")
public class HotCloudServerProperties {

    public static final String HOTCLOUD_SERVER = "${hotcloud.host:hotcloud}:${hotcloud.port:8080}";

    private String host = "hotcloud";
    private Integer port = 8080;

}

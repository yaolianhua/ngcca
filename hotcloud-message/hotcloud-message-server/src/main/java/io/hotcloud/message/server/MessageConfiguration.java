package io.hotcloud.message.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@EnableConfigurationProperties(MessageProperties.class)
@Slf4j
public class MessageConfiguration {

}

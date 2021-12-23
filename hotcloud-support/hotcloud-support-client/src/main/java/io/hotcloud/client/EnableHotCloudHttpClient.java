package io.hotcloud.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({HotCloudHttpClientAutoConfiguration.class})
public @interface EnableHotCloudHttpClient {
}

package io.hotcloud.security;

import io.hotcloud.common.spring.CacheConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        CacheConfiguration.class,
        HotCloudSecurityApplicationTest.class
})
public class HotCloudSecurityApplicationTest {
    //
}

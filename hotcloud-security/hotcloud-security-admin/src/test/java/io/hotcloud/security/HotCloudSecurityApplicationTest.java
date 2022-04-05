package io.hotcloud.security;

import io.hotcloud.common.spring.CacheConfiguration;
import io.hotcloud.db.server.DatabaseProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        CacheConfiguration.class,
        DatabaseProperties.class,
        HotCloudSecurityApplicationTest.class
})
public class HotCloudSecurityApplicationTest {
    //
}

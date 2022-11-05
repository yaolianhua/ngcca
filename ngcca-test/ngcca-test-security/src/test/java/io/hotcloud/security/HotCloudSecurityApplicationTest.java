package io.hotcloud.security;

import io.hotcloud.common.server.cache.CacheConfiguration;
import io.hotcloud.common.server.message.MessageConfiguration;
import io.hotcloud.db.DatabaseProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        CacheConfiguration.class,
        MessageConfiguration.class,
        DatabaseProperties.class,
        HotCloudSecurityApplicationTest.class
})
public class HotCloudSecurityApplicationTest {
    //
}

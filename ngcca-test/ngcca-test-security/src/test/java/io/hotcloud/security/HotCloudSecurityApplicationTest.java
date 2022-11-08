package io.hotcloud.security;

import io.hotcloud.common.server.core.cache.CacheConfiguration;
import io.hotcloud.common.server.core.message.MessageConfiguration;
import io.hotcloud.db.NgccaMongodbProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        CacheConfiguration.class,
        MessageConfiguration.class,
        NgccaMongodbProperties.class,
        HotCloudSecurityApplicationTest.class
})
public class HotCloudSecurityApplicationTest {
    //
}

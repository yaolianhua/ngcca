package io.hotcloud.security;

import io.hotcloud.common.autoconfigure.cache.RedisConfiguration;
import io.hotcloud.common.server.core.message.MessageConfiguration;
import io.hotcloud.db.NgccaMongodbProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        RedisConfiguration.class,
        MessageConfiguration.class,
        NgccaMongodbProperties.class,
        HotCloudSecurityApplicationTest.class
})
public class HotCloudSecurityApplicationTest {
    //
}

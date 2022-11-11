package io.hotcloud.buildpack;

import io.hotcloud.common.autoconfigure.AsyncConfiguration;
import io.hotcloud.common.autoconfigure.cache.RedisConfiguration;
import io.hotcloud.common.server.core.message.MessageConfiguration;
import io.hotcloud.db.NgccaMongodbProperties;
import io.hotcloud.kubernetes.server.KubernetesApiConfiguration;
import io.hotcloud.security.NgccaSecurityRootPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        HotCloudBuildPackApplicationTest.class,
        KubernetesApiConfiguration.class,
        AsyncConfiguration.class,
        MessageConfiguration.class,
        RedisConfiguration.class,
        NgccaMongodbProperties.class,
        NgccaSecurityRootPackage.class
})
public class HotCloudBuildPackApplicationTest {
    //
}

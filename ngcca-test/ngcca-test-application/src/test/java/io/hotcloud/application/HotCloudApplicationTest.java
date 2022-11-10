package io.hotcloud.application;

import io.hotcloud.buildpack.NamedBuildPackPackage;
import io.hotcloud.common.autoconfigure.cache.RedisConfiguration;
import io.hotcloud.common.server.AsyncConfiguration;
import io.hotcloud.common.server.core.message.MessageConfiguration;
import io.hotcloud.db.NgccaMongodbProperties;
import io.hotcloud.kubernetes.server.KubernetesApiConfiguration;
import io.hotcloud.security.NamedSecurityPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                HotCloudApplicationTest.class,
                NamedSecurityPackage.class,
                KubernetesApiConfiguration.class,
                AsyncConfiguration.class,
                MessageConfiguration.class,
                NamedBuildPackPackage.class,
                RedisConfiguration.class,
                NgccaMongodbProperties.class,
                NamedSecurityPackage.class
        }
)
public class HotCloudApplicationTest {
}

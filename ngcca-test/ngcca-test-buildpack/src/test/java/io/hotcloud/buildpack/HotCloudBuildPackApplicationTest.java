package io.hotcloud.buildpack;

import io.hotcloud.common.server.AsyncConfiguration;
import io.hotcloud.common.server.core.cache.CacheConfiguration;
import io.hotcloud.common.server.core.message.MessageConfiguration;
import io.hotcloud.db.DatabaseProperties;
import io.hotcloud.kubernetes.server.KubernetesApiConfiguration;
import io.hotcloud.security.NamedSecurityPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        HotCloudBuildPackApplicationTest.class,
        KubernetesApiConfiguration.class,
        AsyncConfiguration.class,
        MessageConfiguration.class,
        CacheConfiguration.class,
        DatabaseProperties.class,
        NamedSecurityPackage.class
})
public class HotCloudBuildPackApplicationTest {
    //
}

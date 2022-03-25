package io.hotcloud.buildpack;

import io.hotcloud.common.spring.AsyncConfiguration;
import io.hotcloud.common.spring.CacheConfiguration;
import io.hotcloud.kubernetes.server.KubernetesApiConfiguration;
import io.hotcloud.message.server.MessageConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(scanBasePackageClasses = {
        HotCloudBuildPackApplicationTest.class,
        KubernetesApiConfiguration.class,
        AsyncConfiguration.class,
        MessageConfiguration.class,
        CacheConfiguration.class
})
public class HotCloudBuildPackApplicationTest {
    //
}

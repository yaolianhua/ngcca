package io.hotcloud.kubernetes;

import io.hotcloud.common.AsyncConfiguration;
import io.hotcloud.common.message.MessageConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                MessageConfiguration.class,
                AsyncConfiguration.class,
                HotCloudKubernetesApplicationTest.class
        }
)
public class HotCloudKubernetesApplicationTest {
    //
}

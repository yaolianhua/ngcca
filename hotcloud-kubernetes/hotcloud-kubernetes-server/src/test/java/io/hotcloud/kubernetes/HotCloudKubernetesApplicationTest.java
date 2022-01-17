package io.hotcloud.kubernetes;

import io.hotcloud.message.server.MessageConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                MessageConfiguration.class,
                HotCloudKubernetesApplicationTest.class
        }
)
public class HotCloudKubernetesApplicationTest {
    //
}

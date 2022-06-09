package io.hotcloud.kubernetes;

import io.hotcloud.common.api.storage.minio.MinioProperties;
import io.hotcloud.common.server.AsyncConfiguration;
import io.hotcloud.common.server.message.MessageConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                MessageConfiguration.class,
                AsyncConfiguration.class,
                MinioProperties.class,
                HotCloudKubernetesApplicationTest.class
        }
)
public class HotCloudKubernetesApplicationTest {
    //
}

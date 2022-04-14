package io.hotcloud.common.file.storage;

import io.hotcloud.common.file.HotCloudMinioApplicationTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMinioApplicationTest.class
)
@ActiveProfiles("minio-integration-test")
public class MinioIT {
}

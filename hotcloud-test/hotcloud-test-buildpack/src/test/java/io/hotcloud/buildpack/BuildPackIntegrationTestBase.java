package io.hotcloud.buildpack;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudBuildPackApplicationTest.class
)
@ActiveProfiles("buildpack-integration-test-local")
public class BuildPackIntegrationTestBase {
}

package io.hotcloud.kubernetes;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yaolianhua789@gmail.com
 * <p/>
 * Ensure that there is a configuration {@code $HOME/.kube/config} that can access the k8s cluster locally
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudKubernetesApplicationTest.class
)
@ActiveProfiles("integration-test-local")
public class ClientIntegrationTestBase {
}

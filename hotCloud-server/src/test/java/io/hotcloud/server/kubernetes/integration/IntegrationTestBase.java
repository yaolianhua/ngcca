package io.hotcloud.server.kubernetes.integration;

import io.hotcloud.HotCloudApplicationTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yaolianhua789@gmail.com
 * <p>
 * <p/>
 * Ensure that there is a configuration {@code $HOME/.kube/config} that can access the k8s cluster locally
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = HotCloudApplicationTest.class
)
public class IntegrationTestBase {
}

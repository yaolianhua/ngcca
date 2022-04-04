package io.hotcloud.db;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudDatabaseApplicationTest.class
)
@ActiveProfiles("db-integration-test")
public class DatabaseIntegrationTestBase {
}

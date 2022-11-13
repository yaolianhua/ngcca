package io.hotcloud.db;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaDBApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootApplication
public class NgccaDBApplicationTest {

}

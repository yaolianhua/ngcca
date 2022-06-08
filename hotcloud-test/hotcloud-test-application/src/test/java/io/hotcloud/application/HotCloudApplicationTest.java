package io.hotcloud.application;

import io.hotcloud.security.NamedSecurityPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                HotCloudApplicationTest.class,
                NamedSecurityPackage.class
        }
)
public class HotCloudApplicationTest {
}

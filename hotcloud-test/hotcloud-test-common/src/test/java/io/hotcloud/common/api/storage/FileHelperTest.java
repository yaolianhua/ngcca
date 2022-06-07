package io.hotcloud.common.api.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class FileHelperTest {

    @Test
    public void userHome() {
        final String home = FileHelper.getUserHome();
        log.info("user.home {}", home);
    }
}

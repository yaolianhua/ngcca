package io.hotcloud.server.files;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class FileHelperTest {

    @Test
    void userHome() {
        final String home = FileHelper.getUserHome();
        log.info("user.home {}", home);
    }

    @Test
    void filename() {
        String name1 = FileHelper.getFilename("http://127.0.0.1/files/tomcat.tar.gz");
        String name2 = FileHelper.getFilename("files/tomcat.tar.gz");
        String name3 = FileHelper.getFilename("http://127.0.0.1/files/tomcat");
        String name4 = FileHelper.getFilename("http://127.0.0.1/files/tomcat.tgz");

        Assertions.assertEquals("tomcat.tar", name1);
        Assertions.assertEquals("tomcat.tar", name2);
        Assertions.assertEquals("tomcat", name3);
        Assertions.assertEquals("tomcat", name4);
    }
}

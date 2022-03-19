package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.git.GitApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class GitApiIT extends BuildPackIntegrationTestBase {

    @Autowired
    private GitApi gitApi;

    @Test
    public void cloneRepository() throws IOException {

        final String path = "test-clone-repository";

        Boolean cloned = gitApi.clone("https://github.com/GoogleContainerTools/kaniko.git",
                null,
                path,
                null,
                null);
        Assertions.assertTrue(cloned);

        File file = Path.of(path).toFile();
        Assertions.assertNotNull(file);
        log.info("full path: {}", file.getAbsolutePath());
        for (String naming : Objects.requireNonNull(file.list())) {
            log.debug("list of test-clone-repository/kaniko name: {}", naming);
        }

        FileUtils.deleteDirectory(file);
        log.info("deleted repository: {}", file.getAbsolutePath());
    }
}

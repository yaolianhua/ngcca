package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.api.model.GitRepositoryCloned;
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

        GitRepositoryCloned cloned = gitApi.clone("https://gitlab.com/yaolianhua/hotcloud.git",
                null,
                path,
                false,
                null,
                null);
        Assertions.assertTrue(cloned.isSuccess());

        File file = Path.of(path).toFile();
        Assertions.assertNotNull(file);
        log.info("full path: {}", file.getAbsolutePath());
        for (String naming : Objects.requireNonNull(file.list())) {
            log.debug("list of test-clone-repository/hotcloud name: {}", naming);
        }

        GitRepositoryCloned forcedCloned = gitApi.clone("https://gitlab.com/yaolianhua/hotcloud.git",
                null,
                path,
                true,
                null,
                null);
        Assertions.assertTrue(forcedCloned.isSuccess());

        FileUtils.deleteDirectory(file);
        log.info("deleted repository: {}", file.getAbsolutePath());
    }
}

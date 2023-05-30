package io.hotcloud.server.files;

import io.hotcloud.service.files.FileChangeWatcher;
import io.hotcloud.service.files.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class FileChangeWatcherTest {

    CountDownLatch latch = new CountDownLatch(1);
    Path watchPath = Path.of(FileHelper.getUserHome(), "watch_test");

    /**
     * Run this Test before {@link FileChangeWatcherTest#trigger()}
     */
    @Disabled
    @Test
    void watch() throws IOException, InterruptedException {

        FileUtils.deleteDirectory(watchPath.toFile());
        Files.createDirectories(watchPath);
        log.info("Path '{}' created", watchPath);

        FileChangeWatcher watcher = new FileChangeWatcher(watchPath, event -> {
            log.info("context = '{}', event = '{}', type = '{}'", event.context(), event.kind().name(), event.kind().type());

            if ("4.txt".equals(event.context().toString())) {
                latch.countDown();
            }
        });

        watcher.start();
        latch.await();

        FileUtils.deleteDirectory(watchPath.toFile());
        log.info("Path '{}' deleted", watchPath);

        watcher.stop();
    }

    /**
     * Run this Test after {@link FileChangeWatcherTest#watch()}
     */
    @Disabled
    @Test
    void trigger() throws InterruptedException, IOException {

        List<Path> paths = List.of(
                Path.of(watchPath.toString(), "1.txt"),
                Path.of(watchPath.toString(), "2.txt"),
                Path.of(watchPath.toString(), "3.txt"),
                Path.of(watchPath.toString(), "4.txt")
        );

        for (Path path : paths) {
            TimeUnit.SECONDS.sleep(1);
            Files.createFile(path);
            log.info("File '{}' created", path);
        }

    }
}

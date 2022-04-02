package io.hotcloud.common.file;

import io.hotcloud.common.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class FileState {

    private final Path path;

    public FileState(Path path) {
        this.path = path;
    }

    public boolean waitCompleted() {

        Assert.notNull(path, "File path is null", 400);

        File file = path.toFile();
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        if (!file.canRead()) {
            return false;
        }

        long length;
        for (; ; ) {
            length = file.length();
            sleep(30);

            if (length == file.length() && length > 0) {
                sleep(60);
                if (length == file.length()) {
                    log.info("File '{}' write complete, current length '{}'", path, length);
                    return true;
                }

            }
        }

    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            //
        }
    }
}

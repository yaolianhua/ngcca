package io.hotcloud.application.server.processor;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.common.api.storage.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class ApplicationStorageRunnerProcessor implements ApplicationRunnerProcessor {

    @Override
    public void process() {

        try {
            Path volumePath = Path.of(ApplicationConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                log.debug("ApplicationStorageRunnerProcessor. storage path '{}' already exist ", ApplicationConstant.STORAGE_VOLUME_PATH);
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            log.info("ApplicationStorageRunnerProcessor. storage path '{}' created ", directories);
        } catch (IOException e) {
            log.error("ApplicationStorageRunnerProcessor error: {}", e.getCause().getMessage());
        }
    }
}

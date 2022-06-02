package io.hotcloud.application.server.processor;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.storage.FileHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
class ApplicationStorageRunnerProcessor implements ApplicationRunnerProcessor {

    @Override
    public void process() {

        try {
            Path volumePath = Path.of(ApplicationConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                Log.debug(ApplicationStorageRunnerProcessor.class.getName(),
                        String.format("ApplicationStorageRunnerProcessor. storage path '%s' already exist ", ApplicationConstant.STORAGE_VOLUME_PATH));
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            Log.info(ApplicationStorageRunnerProcessor.class.getName(),
                    String.format("ApplicationStorageRunnerProcessor. storage path '%s' created ", directories));
        } catch (IOException e) {
            Log.error(ApplicationStorageRunnerProcessor.class.getName(),
                    String.format("ApplicationStorageRunnerProcessor error: %s", e.getCause().getMessage()));
        }
    }
}

package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.BuildPackRunnerProcessor;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.common.storage.FileHelper;
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
class BuildPackStorageRunnerProcessor implements BuildPackRunnerProcessor {

    @Override
    public void process() {

        try {
            Path volumePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                log.debug("BuildPackStorageRunnerProcessor. storage path '{}' already exist ", BuildPackConstant.STORAGE_VOLUME_PATH);
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            log.info("BuildPackStorageRunnerProcessor. storage path '{}' created ", directories);
        } catch (IOException e) {
            log.error("BuildPackStorageRunnerProcessor error: {}", e.getCause().getMessage());
        }
    }
}

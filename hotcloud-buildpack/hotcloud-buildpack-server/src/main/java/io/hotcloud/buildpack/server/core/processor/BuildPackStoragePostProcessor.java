package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackPostProcessor;
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
class BuildPackStoragePostProcessor implements BuildPackPostProcessor {

    @Override
    public void execute() {

        try {
            Path volumePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                log.debug("BuildPackStoragePostProcessor. storage path '{}' already exist ", BuildPackConstant.STORAGE_VOLUME_PATH);
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            log.info("BuildPackStoragePostProcessor. storage path '{}' created ", directories);
        } catch (IOException e) {
            log.error("BuildPackStoragePostProcessor error: {}", e.getCause().getMessage());
        }
    }
}

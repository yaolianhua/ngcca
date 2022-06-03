package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.BuildPackRunnerProcessor;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
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
class BuildPackStorageRunnerProcessor implements BuildPackRunnerProcessor {

    @Override
    public void process() {

        try {
            Path volumePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                Log.debug(BuildPackStorageRunnerProcessor.class.getName(),
                        String.format("storage path '%s' already exist ", BuildPackConstant.STORAGE_VOLUME_PATH));
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            Log.info(BuildPackStorageRunnerProcessor.class.getName(),
                    String.format("storage path '%s' created ", directories));
        } catch (IOException e) {
            Log.error(BuildPackStorageRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

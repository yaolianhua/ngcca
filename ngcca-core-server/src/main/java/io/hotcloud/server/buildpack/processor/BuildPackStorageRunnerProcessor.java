package io.hotcloud.server.buildpack.processor;

import io.hotcloud.common.model.utils.Log;
import io.hotcloud.server.NGCCARunnerProcessor;
import io.hotcloud.server.files.FileHelper;
import io.hotcloud.vendor.buildpack.BuildPackConstant;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Deprecated(since = "BuildPackApiV2")
class BuildPackStorageRunnerProcessor implements NGCCARunnerProcessor {

    @Override
    public void execute() {

        try {
            Path volumePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                Log.info(BuildPackStorageRunnerProcessor.class.getName(),
                        String.format("BuildPack storage path '%s' already exist ", volumePath));
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            Log.info(BuildPackStorageRunnerProcessor.class.getName(),
                    String.format("BuildPack storage path '%s' created ", directories));
        } catch (IOException e) {
            Log.error(BuildPackStorageRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

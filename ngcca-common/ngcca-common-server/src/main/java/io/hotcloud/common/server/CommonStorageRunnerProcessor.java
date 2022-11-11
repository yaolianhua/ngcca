package io.hotcloud.common.server;

import io.hotcloud.common.api.NGCCARunnerProcessor;
import io.hotcloud.common.api.core.files.FileHelper;
import io.hotcloud.common.model.utils.Log;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.hotcloud.common.model.CommonConstant.ROOT_PATH;

@Component
public class CommonStorageRunnerProcessor implements NGCCARunnerProcessor {

    @Override
    public void execute() {
        try {
            Path volumePath = Path.of(ROOT_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                Log.info(CommonStorageRunnerProcessor.class.getName(),
                        String.format("Root storage path '%s' already exist ", ROOT_PATH));
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            Log.info(CommonStorageRunnerProcessor.class.getName(),
                    String.format("Root storage path '%s' created ", directories));
        } catch (IOException e) {
            Log.error(CommonStorageRunnerProcessor.class.getName(),
                    String.format("%s", e.getMessage()));
        }
    }
}

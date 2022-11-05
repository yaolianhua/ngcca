package io.hotcloud.common.server.storage;

import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.storage.FileHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.hotcloud.common.api.CommonConstant.ROOT_PATH;
@Component
public class CommonStorageRunnerProcessor implements CommonRunnerProcessor {

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

package io.hotcloud.server;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.server.files.FileHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.hotcloud.common.model.CommonConstant.ROOT_PATH;

@Component
public class GlobalRootPathInitialization implements NGCCARunnerProcessor {

    @Override
    public void execute() {
        try {
            Path volumePath = Path.of(ROOT_PATH);
            boolean exists = FileHelper.exists(volumePath);

            if (exists) {
                Log.info(this, null, Event.START,
                        String.format("Root storage path '%s' already exist ", ROOT_PATH));
                return;
            }
            Path directories = Files.createDirectories(volumePath);
            Log.info(this, null, Event.START,
                    String.format("Root storage path '%s' created ", directories));
        } catch (IOException e) {
            Log.error(this, null, Event.START, e.getMessage());
        }
    }
}

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
public class SystemWorkspaceRunnerProcessor implements RunnerProcessor {

    @Override
    public void execute() {
        try {
            Path path = Path.of(ROOT_PATH);
            boolean exists = FileHelper.exists(path);
            if (!exists) {
                Path directories = Files.createDirectories(path);
                Log.info(this, null, Event.START, String.format("init app workspace dir. '%s'", directories));
            }
        } catch (IOException e) {
            Log.error(this, null, Event.START, e.getMessage());
        }
    }
}

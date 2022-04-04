package io.hotcloud.buildpack.server.buildpack.processor;

import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.buildpack.api.KanikoFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class BuildPackApplicationDefaultArgsPrinterPostProcessor implements BuildPackApplicationRunnerPostProcessor {

    private final KanikoFlag kanikoFlag;

    public BuildPackApplicationDefaultArgsPrinterPostProcessor(KanikoFlag kanikoFlag) {
        this.kanikoFlag = kanikoFlag;
    }

    @Override
    public void execute() {
        Map<String, String> args = kanikoFlag.resolvedArgs();

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            builder.append("- --").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        log.info("BuildPack default args printer post processor. logged kaniko flags \n {}", builder);

    }
}

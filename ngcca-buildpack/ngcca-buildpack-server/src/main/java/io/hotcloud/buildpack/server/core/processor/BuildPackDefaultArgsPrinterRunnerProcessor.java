package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.core.KanikoFlag;
import io.hotcloud.common.api.NGCCARunnerProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
@Deprecated(since = "BuildPackApiV2")
class BuildPackDefaultArgsPrinterRunnerProcessor implements NGCCARunnerProcessor {

    private final KanikoFlag kanikoFlag;

    public BuildPackDefaultArgsPrinterRunnerProcessor(KanikoFlag kanikoFlag) {
        this.kanikoFlag = kanikoFlag;
    }

    @Override
    public void execute() {
        Map<String, String> args = kanikoFlag.resolvedArgs();

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            builder.append("- --").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        log.debug("logged kaniko args \n {}", builder);

    }
}

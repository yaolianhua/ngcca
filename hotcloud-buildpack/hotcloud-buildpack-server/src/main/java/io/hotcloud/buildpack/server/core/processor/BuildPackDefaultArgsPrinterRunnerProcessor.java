package io.hotcloud.buildpack.server.core.processor;

import io.hotcloud.buildpack.api.BuildPackRunnerProcessor;
import io.hotcloud.buildpack.api.core.KanikoFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class BuildPackDefaultArgsPrinterRunnerProcessor implements BuildPackRunnerProcessor {

    private final KanikoFlag kanikoFlag;

    public BuildPackDefaultArgsPrinterRunnerProcessor(KanikoFlag kanikoFlag) {
        this.kanikoFlag = kanikoFlag;
    }

    @Override
    public void process() {
        Map<String, String> args = kanikoFlag.resolvedArgs();

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            builder.append("- --").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        log.debug("BuildPackDefaultArgsPrinterRunnerProcessor. logged kaniko flags \n {}", builder);

    }
}

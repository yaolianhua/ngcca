package io.hotcloud.common.server;

import io.hotcloud.common.api.CommonRunnerProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonApplicationRunner implements ApplicationRunner {

    private final List<CommonRunnerProcessor> processors;

    public CommonApplicationRunner(List<CommonRunnerProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (CommonRunnerProcessor processor : processors) {
            processor.execute();
        }

    }
}

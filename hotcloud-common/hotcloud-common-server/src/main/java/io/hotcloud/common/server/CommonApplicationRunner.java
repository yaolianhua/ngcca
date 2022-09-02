package io.hotcloud.common.server;

import io.hotcloud.common.api.CommonRunnerProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 3)
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

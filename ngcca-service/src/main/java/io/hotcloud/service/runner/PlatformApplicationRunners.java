package io.hotcloud.service.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 3)
public class PlatformApplicationRunners implements ApplicationRunner {

    private final List<RunnerProcessor> processors;

    public PlatformApplicationRunners(List<RunnerProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (RunnerProcessor processor : processors) {
            processor.execute();
        }

    }
}

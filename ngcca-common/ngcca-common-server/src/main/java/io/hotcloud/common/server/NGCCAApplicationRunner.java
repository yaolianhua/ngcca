package io.hotcloud.common.server;

import io.hotcloud.common.api.NGCCARunnerProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 3)
public class NGCCAApplicationRunner implements ApplicationRunner {

    private final List<NGCCARunnerProcessor> processors;

    public NGCCAApplicationRunner(List<NGCCARunnerProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (NGCCARunnerProcessor processor : processors) {
            processor.execute();
        }

    }
}

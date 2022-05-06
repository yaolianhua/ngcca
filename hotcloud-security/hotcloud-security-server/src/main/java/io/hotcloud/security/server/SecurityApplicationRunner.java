package io.hotcloud.security.server;

import io.hotcloud.security.SecurityRunnerProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class SecurityApplicationRunner implements ApplicationRunner {

    private final List<SecurityRunnerProcessor> processors;

    public SecurityApplicationRunner(List<SecurityRunnerProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (SecurityRunnerProcessor processor : processors) {
            processor.execute();
        }

    }
}

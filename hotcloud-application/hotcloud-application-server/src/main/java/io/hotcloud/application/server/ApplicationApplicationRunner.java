package io.hotcloud.application.server;

import io.hotcloud.application.api.ApplicationRunnerProcessor;
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
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class ApplicationApplicationRunner implements ApplicationRunner {

    private final List<ApplicationRunnerProcessor> applicationRunnerProcessors;

    public ApplicationApplicationRunner(List<ApplicationRunnerProcessor> applicationRunnerProcessors) {
        this.applicationRunnerProcessors = applicationRunnerProcessors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (ApplicationRunnerProcessor postProcessor : applicationRunnerProcessors) {
            postProcessor.process();
        }
    }
}

package io.hotcloud.application.server;

import io.hotcloud.application.api.ApplicationPostProcessor;
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

    private final List<ApplicationPostProcessor> applicationPostProcessors;

    public ApplicationApplicationRunner(List<ApplicationPostProcessor> applicationPostProcessors) {
        this.applicationPostProcessors = applicationPostProcessors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (ApplicationPostProcessor postProcessor : applicationPostProcessors) {
            postProcessor.execute();
        }
    }
}

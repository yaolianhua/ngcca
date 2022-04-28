package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.api.core.BuildPackPostProcessor;
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
public class BuildPackApplicationRunner implements ApplicationRunner {

    private final List<BuildPackPostProcessor> buildPackPostProcessors;

    public BuildPackApplicationRunner(List<BuildPackPostProcessor> buildPackPostProcessors) {
        this.buildPackPostProcessors = buildPackPostProcessors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (BuildPackPostProcessor postProcessor : buildPackPostProcessors) {
            postProcessor.execute();
        }
    }
}

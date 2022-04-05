package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
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

    private final List<BuildPackApplicationRunnerPostProcessor> buildPackApplicationRunnerPostProcessors;

    public BuildPackApplicationRunner(List<BuildPackApplicationRunnerPostProcessor> buildPackApplicationRunnerPostProcessors) {
        this.buildPackApplicationRunnerPostProcessors = buildPackApplicationRunnerPostProcessors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (BuildPackApplicationRunnerPostProcessor postProcessor : buildPackApplicationRunnerPostProcessors) {
            postProcessor.execute();
        }
    }
}

package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.api.BuildPackRunnerPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackRunner implements ApplicationRunner {

    private final List<BuildPackRunnerPostProcessor> buildPackRunnerPostProcessors;

    public BuildPackRunner(List<BuildPackRunnerPostProcessor> buildPackRunnerPostProcessors) {
        this.buildPackRunnerPostProcessors = buildPackRunnerPostProcessors;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (BuildPackRunnerPostProcessor postProcessor : buildPackRunnerPostProcessors) {
            postProcessor.execute();
        }
    }
}

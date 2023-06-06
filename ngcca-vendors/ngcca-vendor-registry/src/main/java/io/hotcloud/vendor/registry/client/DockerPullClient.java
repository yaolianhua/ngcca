package io.hotcloud.vendor.registry.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.DockerProperties;
import io.hotcloud.vendor.registry.model.DockerClientCreateConfig;
import io.hotcloud.vendor.registry.model.RegistryImage;
import io.hotcloud.vendor.registry.model.RegistryUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class DockerPullClient {

    private final DockerProperties dockerProperties;
    private final ExecutorService executorService;

    public DockerPullClient(DockerProperties dockerProperties, ExecutorService executorService) {
        this.dockerProperties = dockerProperties;
        this.executorService = executorService;
    }

    /**
     * docker pull
     *
     * @param target 目标镜像参数对象
     */
    public boolean pull(RegistryImage target) {

        DockerClientCreateConfig dockerClientCreateConfig = DockerClientCreateConfig.builder()
                .host(dockerProperties.getHost())
                .tlsVerify(false)
                .build();

        StopWatch watch = new StopWatch();
        watch.start();

        try (DockerClient dockerClient = DockerClientFactory.create(dockerClientCreateConfig)) {

            Log.debug(this, dockerClientCreateConfig, "create docker client");

            String registry = RegistryUtil.getRegistry(target.getName());
            AuthConfig authConfig = new AuthConfig();
            authConfig.withRegistryAddress(registry);
            if (Objects.nonNull(target.getAuthentication())) {
                authConfig.withUsername(target.getAuthentication().getUsername());
                authConfig.withPassword(target.getAuthentication().getPassword());
            }
            Log.debug(this, authConfig, "init registry auth config");
            Future<Boolean> future = executorService.submit(() -> {
                try {
                    return dockerClient.pullImageCmd(target.getName())
                            .withAuthConfig(authConfig)
                            .exec(new PullImageResultCallback())
                            .awaitCompletion(dockerProperties.getPullTimeoutSeconds(), TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            });
            for (; ; ) {
                if (future.isDone()) {
                    watch.stop();

                    if (Boolean.TRUE.equals(future.get())) {
                        Log.info(this, target, "image pull success, times " + watch.getTotalTimeSeconds() + "s");
                        return true;
                    }
                    Log.warn(this, target, "image pull failed, times " + watch.getTotalTimeSeconds() + "s");
                    return false;
                }
            }

        } catch (IOException | ExecutionException e) {
            throw new PlatformException(e.getMessage(), 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlatformException(e.getMessage(), 500);
        }
    }
}

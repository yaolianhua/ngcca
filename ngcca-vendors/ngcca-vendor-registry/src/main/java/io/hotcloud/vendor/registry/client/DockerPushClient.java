package io.hotcloud.vendor.registry.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.PushImageResultCallback;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.DockerProperties;
import io.hotcloud.vendor.registry.model.RegistryImage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class DockerPushClient {

    private final DockerProperties dockerProperties;
    private final ExecutorService executorService;
    private final DockerPullClient dockerPullClient;
    private final DockerClient dockerClient;

    public DockerPushClient(DockerProperties dockerProperties,
                            ExecutorService executorService,
                            DockerPullClient dockerPullClient,
                            DockerClient dockerClient) {
        this.dockerProperties = dockerProperties;
        this.executorService = executorService;
        this.dockerPullClient = dockerPullClient;
        this.dockerClient = dockerClient;
    }

    /**
     * docker push
     *
     * @param source 源镜像参数对象
     * @param target 目标镜像参数对象
     */
    public boolean push(RegistryImage source, RegistryImage target) {
        Assert.notNull(source, "source image param is null");
        Assert.notNull(target, "target image param is null");

        StopWatch watch = new StopWatch();
        watch.start();

        boolean pulled = dockerPullClient.pull(source);
        if (!pulled) {
            Log.warn(this, source, "image push failed [pull failed]");
            return false;
        }

        try {
            AuthConfig authConfig = new AuthConfig();
            authConfig.withRegistryAddress(target.getRegistry());
            if (Objects.nonNull(target.getAuthentication())) {
                authConfig.withUsername(target.getAuthentication().getUsername());
                authConfig.withPassword(target.getAuthentication().getPassword());
            }
            Log.debug(this, authConfig, "[push]init target registry auth config");

            dockerClient.tagImageCmd(source.getName(), target.getRegistryImageWithNoTag(), target.getImageTag()).exec();
            Log.info(this, target, "[push]tag image success");

            Future<Boolean> future = executorService.submit(
                    () -> dockerClient.pushImageCmd(target.getName())
                            .withAuthConfig(authConfig)
                            .exec(new PushImageResultCallback())
                            .awaitCompletion(dockerProperties.getPushTimeoutSeconds(), TimeUnit.SECONDS));

            for (; ; ) {
                if (future.isDone()) {
                    watch.stop();

                    if (Boolean.TRUE.equals(future.get())) {
                        Log.info(this, target, "image push success, times " + watch.getTotalTimeSeconds() + "s");
                        return true;
                    }
                    Log.warn(this, target, "image push failed, times " + watch.getTotalTimeSeconds() + "s");
                    return false;
                }
            }
        } catch (ExecutionException e) {
            throw new PlatformException(e.getMessage(), 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlatformException(e.getMessage(), 500);
        }
    }
}

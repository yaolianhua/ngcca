package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageProperties;
import io.hotcloud.common.storage.minio.MinioBucketApi;
import io.hotcloud.common.storage.minio.MinioObjectApi;
import io.hotcloud.common.storage.minio.MinioProperties;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
@Slf4j
public class BuildPackRabbitMQMessageSubscriber {

    private final MinioObjectApi minioObjectApi;
    private final MinioBucketApi minioBucketApi;

    private final MinioProperties minioProperties;

    private final BuildPackService buildPackService;
    private final BuildPackPlayer buildPackPlayer;
    private final GitClonedService gitClonedService;
    private final ObjectMapper objectMapper;

    public BuildPackRabbitMQMessageSubscriber(MinioObjectApi minioObjectApi,
                                              MinioBucketApi minioBucketApi,
                                              MinioProperties minioProperties,
                                              BuildPackService buildPackService,
                                              BuildPackPlayer buildPackPlayer,
                                              GitClonedService gitClonedService,
                                              ObjectMapper objectMapper) {
        this.minioObjectApi = minioObjectApi;
        this.minioBucketApi = minioBucketApi;
        this.minioProperties = minioProperties;
        this.buildPackService = buildPackService;
        this.buildPackPlayer = buildPackPlayer;
        this.gitClonedService = gitClonedService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = SecurityConstant.QUEUE_BUILDPACK_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = SecurityConstant.EXCHANGE_FANOUT_SECURITY_MESSAGE)
                    )
            }
    )
    public void userDeleted(String message) {
        Message<User> messageBody = convertUserMessageBody(message);
        User user = messageBody.getData();
        log.info("[BuildPackRabbitMQMessageSubscriber] received [{}] user deleted message", user.getUsername());
        List<BuildPack> buildPacks = buildPackService.findAll(user.getUsername());
        for (BuildPack buildPack : buildPacks) {
            buildPackPlayer.delete(buildPack.getId(), true);
        }
        log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user {} buildPacks has been deleted", user.getUsername(), buildPacks.size());

        List<GitCloned> cloneds = gitClonedService.listCloned(user.getUsername());
        log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user {} cloned repositories has been deleted", user.getUsername(), cloneds.size());
        gitClonedService.delete(user.getUsername());

    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = BuildPackConstant.QUEUE_SUBSCRIBE_BUILDPACK_DONE_MESSAGE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = BuildPackConstant.EXCHANGE_FANOUT_BUILDPACK_MESSAGE)
                    )
            }
    )
    public void subscribe(String message) {
        Message<BuildPack> messageBody = convertBuildPackMessageBody(message);
        BuildPack buildPack = messageBody.getData();
        log.info("[BuildPackRabbitMQMessageSubscriber] received [{}] user's BuildPack '{}' message", buildPack.getUser(), buildPack.getId());

        StopWatch watch = new StopWatch();
        watch.start();
        try {
            String namespace = buildPack.getJobResource().getNamespace();
            String clonedPath = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
            String tarball = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

            File file = Path.of(clonedPath, tarball).toFile();
            if (!file.exists()) {
                log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user's BuildPack tarBall '{}' dose not exist!", buildPack.getUser(), tarball);
                return;
            }
            log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user's BuildPack tarBall '{}' size '{}MB'", buildPack.getUser(), tarball, DataSize.ofBytes(file.length()).toMegabytes());

            if (!minioBucketApi.exist(namespace)) {
                minioBucketApi.make(namespace);
            }

            String objectname = minioObjectApi.uploadFile(namespace, tarball, file.getAbsolutePath());

            watch.stop();
            log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user's BuildPack tarBall '{}' upload success. takes '{}s'", buildPack.getUser(), tarball, watch.getTotalTimeSeconds());

            buildPack.setArtifact(Path.of(minioProperties.getEndpoint(), namespace, objectname).toString());
            buildPackService.saveOrUpdate(buildPack);
        } catch (Exception ex) {
            log.error("[BuildPackRabbitMQMessageSubscriber] error. {}", ex.getMessage());
        }

    }

    private Message<BuildPack> convertBuildPackMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }

    private Message<User> convertUserMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }
}

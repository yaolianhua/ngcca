package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.model.DefaultBuildPack;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageProperties;
import io.hotcloud.common.storage.minio.MinioBucketApi;
import io.hotcloud.common.storage.minio.MinioObjectApi;
import io.hotcloud.common.storage.minio.MinioProperties;
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
    private final ObjectMapper objectMapper;

    public BuildPackRabbitMQMessageSubscriber(MinioObjectApi minioObjectApi,
                                              MinioBucketApi minioBucketApi,
                                              MinioProperties minioProperties,
                                              BuildPackService buildPackService,
                                              ObjectMapper objectMapper) {
        this.minioObjectApi = minioObjectApi;
        this.minioBucketApi = minioBucketApi;
        this.minioProperties = minioProperties;
        this.buildPackService = buildPackService;
        this.objectMapper = objectMapper;
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
        Message<DefaultBuildPack> messageBody = convertBuildPackMessageBody(message);
        DefaultBuildPack buildPack = messageBody.getData();
        log.info("[BuildPackRabbitMQMessageSubscriber] received buildPack '{}' mq message", buildPack.getId());

        StopWatch watch = new StopWatch();
        watch.start();
        try {
            String namespace = buildPack.getJobResource().getNamespace();
            String clonedPath = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
            String tarball = buildPack.getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

            File file = Path.of(clonedPath, tarball).toFile();
            log.info("[BuildPackRabbitMQMessageSubscriber] BuildPack tarBall '{}' size '{}MB'", tarball, DataSize.ofBytes(file.length()).toMegabytes());

            if (!minioBucketApi.exist(namespace)) {
                minioBucketApi.make(namespace);
            }

            String objectname = minioObjectApi.uploadFile(namespace, tarball, file.getAbsolutePath());

            watch.stop();
            log.info("[BuildPackRabbitMQMessageSubscriber] BuildPack tarBall '{}' upload success. takes '{}s'", tarball, watch.getTotalTimeSeconds());

            buildPack.setArtifact(Path.of(minioProperties.getEndpoint(), namespace, objectname).toString());
            buildPackService.saveOrUpdate(buildPack);
        } catch (Exception ex) {
            log.error("[BuildPackRabbitMQMessageSubscriber] error. {}", ex.getMessage());
        }

    }

    private Message<DefaultBuildPack> convertBuildPackMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }
}

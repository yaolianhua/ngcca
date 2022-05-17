package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageProperties;
import io.hotcloud.common.storage.FileHelper;
import io.hotcloud.common.storage.minio.MinioBucketApi;
import io.hotcloud.common.storage.minio.MinioObjectApi;
import io.hotcloud.common.storage.minio.MinioProperties;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.UserNamespacePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
    private final NamespaceApi namespaceApi;
    private final ObjectMapper objectMapper;

    public BuildPackRabbitMQMessageSubscriber(MinioObjectApi minioObjectApi,
                                              MinioBucketApi minioBucketApi,
                                              MinioProperties minioProperties,
                                              BuildPackService buildPackService,
                                              BuildPackPlayer buildPackPlayer,
                                              GitClonedService gitClonedService,
                                              NamespaceApi namespaceApi,
                                              ObjectMapper objectMapper) {
        this.minioObjectApi = minioObjectApi;
        this.minioBucketApi = minioBucketApi;
        this.minioProperties = minioProperties;
        this.buildPackService = buildPackService;
        this.buildPackPlayer = buildPackPlayer;
        this.gitClonedService = gitClonedService;
        this.namespaceApi = namespaceApi;
        this.objectMapper = objectMapper;
    }

    /*
    http://120.78.225.168:9009/10908e84eee54ee88e473da577080f5a/devops-thymeleaf-20220422190103.tar
     */
    private static String retrieveMinioObjectname(String artifact) {
        int index = artifact.lastIndexOf("/");
        return artifact.substring(index + 1);
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

        Message<UserNamespacePair> messageBody = convertUserMessageBody(message);
        UserNamespacePair pair = messageBody.getData();
        log.info("[BuildPackRabbitMQMessageSubscriber] received [{}] user deleted message", pair.getUsername());

        //remove buildPack record
        List<BuildPack> buildPacks = buildPackService.findAll(pair.getUsername());
        try {
            buildPacks.forEach(e -> buildPackPlayer.delete(e.getId(), true));
        } catch (Exception e) {
            log.info("[BuildPackRabbitMQMessageSubscriber] delete buildPack error. {}", e.getMessage());
        }
        log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user {} buildPacks has been deleted", pair.getUsername(), buildPacks.size());

        //remove git cloned record
        List<GitCloned> cloneds = gitClonedService.findAll(pair.getUsername());
        gitClonedService.delete(pair.getUsername());
        log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user {} cloned repositories has been deleted", pair.getUsername(), cloneds.size());

        //remove minio data
        List<String> objectnames = buildPacks.stream()
                .map(BuildPack::getArtifact)
                .filter(StringUtils::hasText)
                .map(BuildPackRabbitMQMessageSubscriber::retrieveMinioObjectname)
                .collect(Collectors.toList());
        try {
            for (String objectname : objectnames) {
                minioObjectApi.removed(pair.getNamespace(), objectname);
            }
            minioBucketApi.remove(pair.getNamespace());
            log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user '{}' object storage has been deleted", pair.getUsername(), pair.getNamespace());
        } catch (Exception e) {
            log.info("[BuildPackRabbitMQMessageSubscriber] delete object storage error. {}", e.getMessage());
        }

        try {
            //remove persistent data
            Path localPath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH, pair.getNamespace());
            FileHelper.deleteRecursively(localPath);
            log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user '{}' local storage has been deleted", pair.getUsername(), localPath);
        } catch (Exception e) {
            log.info("[BuildPackRabbitMQMessageSubscriber] delete local storage error. {}", e.getMessage());
        }

        try {
            Namespace namespace = namespaceApi.read(pair.getNamespace());
            if (namespace != null) {
                namespaceApi.delete(pair.getNamespace());
            }
            log.info("[BuildPackRabbitMQMessageSubscriber] [{}] user namespace [{}] has been deleted", pair.getUsername(), pair.getNamespace());
        } catch (Exception e) {
            log.info("[BuildPackRabbitMQMessageSubscriber] delete namespace error. {}", e.getMessage());
        }

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
        log.info("[BuildPackRabbitMQMessageSubscriber] received [{}] user's BuildPack '{}' done message", buildPack.getUser(), buildPack.getId());

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


            buildPack.setArtifact(String.format("%s/%s/%s", minioProperties.getEndpoint(), namespace, objectname));
            buildPackService.saveOrUpdate(buildPack);
        } catch (Exception ex) {
            log.error("[BuildPackRabbitMQMessageSubscriber] handle buildPack done error. {}", ex.getMessage());
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

    private Message<UserNamespacePair> convertUserMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }
}

package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Log;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.hotcloud.buildpack.api.core.ImageBuildStatus.Failed;
import static io.hotcloud.buildpack.api.core.ImageBuildStatus.Succeeded;
import static io.hotcloud.common.model.CommonConstant.*;

@Component
@ConditionalOnProperty(
        name = BuildPackImagebuildProperties.PROPERTIES_TYPE_NAME,
        havingValue = BuildPackImagebuildProperties.RABBITMQ
)
@RequiredArgsConstructor
@Slf4j
public class BuildPackRabbitMQK8sEventsListener {
    private final ObjectMapper objectMapper;
    private final BuildPackService buildPackService;
    private final BuildPackApiV2 buildPackApiV2;
    private final ImageBuildCacheApi imageBuildCacheApi;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_KUBERNETES_WORKLOADS_JOB_BUILDPACK),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB)
                    )
            }
    )
    public void subscribe(String message) {

        try {
            WatchMessageBody messageBody = convertWatchMessageBody(message).getData();
            if (!Objects.equals(WorkloadsType.Job.name(), messageBody.getKind())){
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)){
                return;
            }
            BuildPack fetched = buildPackService.findByUuid(businessId);
            if (Objects.isNull(fetched)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())){
                log.info("BuildPack Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                //ignore
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())){
                if (fetched.isDone() || fetched.isDeleted()) {
                    return;
                }
                log.info("BuildPack [{}] {} events: {}/{}/{}", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                this.watch(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())){
                log.info("BuildPack error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        }catch (Exception e){
            Log.error(BuildPackRabbitMQK8sEventsListener.class.getName(), e.getMessage());
        }

    }

    private Message<WatchMessageBody> convertWatchMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCACommonException(e.getMessage());
        }
    }

    private void watch(BuildPack buildPack) {

        String namespace = buildPack.getJobResource().getNamespace();
        String job = buildPack.getJobResource().getName();
        imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Unknown);
        boolean timeout = LocalDateTime.now().compareTo(buildPack.getCreatedAt().plusSeconds(imageBuildCacheApi.getTimeoutSeconds())) > 0;
        try {
            if (timeout) {
                buildPack.setDone(true);
                buildPack.setMessage(TIMEOUT_MESSAGE);
                buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));

                buildPackService.saveOrUpdate(buildPack);
                imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);

                return;
            }

            ImageBuildStatus status = buildPackApiV2.getStatus(namespace, job);
            if (Objects.equals(Succeeded, status) || Objects.equals(Failed, status)) {
                buildPack.setDone(true);
                buildPack.setMessage(Objects.equals(Succeeded, status) ? SUCCESS_MESSAGE : FAILED_MESSAGE);
                buildPack.setLogs(buildPackApiV2.fetchLog(namespace, job));
                buildPackService.saveOrUpdate(buildPack);

                imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
                return;
            }

            Log.info(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild][%s]. namespace:%s | job:%s | buildPack:%s", status, namespace, job, buildPack.getId()));
            imageBuildCacheApi.setStatus(buildPack.getId(), status);

        } catch (Exception ex) {
            Log.error(BuildPackRabbitMQK8sEventsListener.class.getName(), String.format("[ImageBuild] exception occur, namespace:%s | job:%s | buildPack:%s | message:%s", namespace, job, buildPack.getId(), ex.getMessage()));

            buildPack.setDone(true);
            buildPack.setMessage("exception occur: " + ex.getMessage());
            buildPackService.saveOrUpdate(buildPack);
            imageBuildCacheApi.setStatus(buildPack.getId(), ImageBuildStatus.Failed);
        }
    }

}

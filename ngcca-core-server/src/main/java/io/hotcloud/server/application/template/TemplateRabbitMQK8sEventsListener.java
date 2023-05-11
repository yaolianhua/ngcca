package io.hotcloud.server.application.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstancePlayer;
import io.hotcloud.module.application.template.TemplateInstanceService;
import io.hotcloud.server.message.MessageObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateRabbitMQK8sEventsListener implements MessageObserver {
    private final ObjectMapper objectMapper;
    private final TemplateInstanceService templateInstanceService;
    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateDeploymentWatchService templateDeploymentWatchService;

    @Override
    public void onMessage(Message<?> message) {
        //TODO
    }

    public void subscribe(String message) {

        try {
            WatchMessageBody messageBody = convertWatchMessageBody(message).getData();
            if (!Objects.equals(WorkloadsType.Deployment.name(), messageBody.getKind())) {
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)) {
                return;
            }
            TemplateInstance template = templateInstanceService.findByUuid(businessId);
            if (Objects.isNull(template)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())) {
                log.info("Template Delete events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                templateInstancePlayer.delete(template.getId());
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())) {
                if (template.isSuccess()) {
                    return;
                }
                log.info("Template [{}] {} events: {}/{}/{}", template.getId(), messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
                templateDeploymentWatchService.mqWatch(template);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())) {
                log.info("Application error events: {}/{}/{}", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName());
            }
        } catch (Exception e) {
            Log.error(this, null, e.getMessage());
        }

    }

    private Message<WatchMessageBody> convertWatchMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException(e.getMessage());
        }
    }

}

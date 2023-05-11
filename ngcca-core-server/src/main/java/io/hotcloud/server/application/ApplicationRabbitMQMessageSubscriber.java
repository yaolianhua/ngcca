package io.hotcloud.server.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Message;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstancePlayer;
import io.hotcloud.module.application.template.TemplateInstanceService;
import io.hotcloud.module.security.user.UserNamespacePair;
import io.hotcloud.server.files.FileHelper;
import io.hotcloud.server.message.MessageObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

import static io.hotcloud.common.model.CommonConstant.ROOT_PATH;

@Component
@RequiredArgsConstructor
public class ApplicationRabbitMQMessageSubscriber implements MessageObserver {

    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateInstanceService templateInstanceService;

    private final NamespaceClient namespaceApi;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message<?> message) {
        //TODO
    }

    public void userDeleted(String message) {

        Message<UserNamespacePair> messageBody = convertUserMessageBody(message);
        UserNamespacePair pair = messageBody.getData();
        Log.info(this, message,
                String.format("[ApplicationRabbitMQMessageSubscriber] received [%s] user deleted message", pair.getUsername()));
        List<TemplateInstance> templateInstances = templateInstanceService.findAll(pair.getUsername());
        try {
            for (TemplateInstance template : templateInstances) {
                templateInstancePlayer.delete(template.getId());
            }
            Log.info(this, message,
                    String.format("[ApplicationRabbitMQMessageSubscriber] [%s] user %s instance template has been deleted", pair.getUsername(), templateInstances.size()));
        } catch (Exception e) {
            Log.error(this, message,
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete instance template error. %s", e.getMessage()));
        }

        try {
            FileHelper.deleteRecursively(Path.of(ROOT_PATH, pair.getNamespace()));
        } catch (Exception e) {
            Log.error(this, message,
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete local storage error. %s", e.getMessage()));
        }

        try {
            Namespace namespace = namespaceApi.read(pair.getNamespace());
            if (namespace != null) {
                namespaceApi.delete(pair.getNamespace());
            }
            Log.info(this, message,
                    String.format("[ApplicationRabbitMQMessageSubscriber] [%s] user namespace [%s] has been deleted", pair.getUsername(), pair.getNamespace()));
        } catch (Exception e) {
            Log.error(this, message,
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete namespace error. %s", e.getMessage()));
        }

    }

    private Message<UserNamespacePair> convertUserMessageBody(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {
            });

        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException(e.getMessage());
        }
    }
}

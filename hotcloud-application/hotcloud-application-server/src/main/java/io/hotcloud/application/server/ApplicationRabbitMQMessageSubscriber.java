package io.hotcloud.application.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.message.Message;
import io.hotcloud.common.api.message.MessageProperties;
import io.hotcloud.common.api.storage.FileHelper;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.UserNamespacePair;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
public class ApplicationRabbitMQMessageSubscriber {

    private final InstanceTemplatePlayer instanceTemplatePlayer;
    private final InstanceTemplateService instanceTemplateService;

    private final NamespaceApi namespaceApi;

    private final ObjectMapper objectMapper;

    public ApplicationRabbitMQMessageSubscriber(InstanceTemplatePlayer instanceTemplatePlayer,
                                                InstanceTemplateService instanceTemplateService,
                                                NamespaceApi namespaceApi,
                                                ObjectMapper objectMapper) {
        this.instanceTemplatePlayer = instanceTemplatePlayer;
        this.instanceTemplateService = instanceTemplateService;
        this.namespaceApi = namespaceApi;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = SecurityConstant.QUEUE_APPLICATION_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = SecurityConstant.EXCHANGE_FANOUT_SECURITY_MESSAGE)
                    )
            }
    )
    public void userDeleted(String message) {

        Message<UserNamespacePair> messageBody = convertUserMessageBody(message);
        UserNamespacePair pair = messageBody.getData();
        Log.info(ApplicationRabbitMQMessageSubscriber.class.getName(),
                String.format("[ApplicationRabbitMQMessageSubscriber] received [%s] user deleted message", pair.getUsername()));
        List<InstanceTemplate> instanceTemplates = instanceTemplateService.findAll(pair.getUsername());
        try {
            for (InstanceTemplate template : instanceTemplates) {
                instanceTemplatePlayer.delete(template.getId());
            }
            Log.info(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] [%s] user %s instance template has been deleted", pair.getUsername(), instanceTemplates.size()));
        } catch (Exception e) {
            Log.error(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete instance template error. %s", e.getMessage()));
        }

        try {
            FileHelper.deleteRecursively(Path.of(ApplicationConstant.STORAGE_VOLUME_PATH, pair.getNamespace()));
        } catch (Exception e) {
            Log.error(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete local storage error. %s", e.getMessage()));
        }

        try {
            Namespace namespace = namespaceApi.read(pair.getNamespace());
            if (namespace != null) {
                namespaceApi.delete(pair.getNamespace());
            }
            Log.info(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] [%s] user namespace [%s] has been deleted", pair.getUsername(), pair.getNamespace()));
        } catch (Exception e) {
            Log.error(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete namespace error. %s", e.getMessage()));
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

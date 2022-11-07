package io.hotcloud.application.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.message.Message;
import io.hotcloud.common.api.message.MessageProperties;
import io.hotcloud.common.api.storage.FileHelper;
import io.hotcloud.kubernetes.api.NamespaceApi;
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

import static io.hotcloud.common.api.CommonConstant.ROOT_PATH;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.RABBITMQ
)
public class ApplicationRabbitMQMessageSubscriber {

    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateInstanceService templateInstanceService;

    private final NamespaceApi namespaceApi;

    private final ObjectMapper objectMapper;

    public ApplicationRabbitMQMessageSubscriber(TemplateInstancePlayer templateInstancePlayer,
                                                TemplateInstanceService templateInstanceService,
                                                NamespaceApi namespaceApi,
                                                ObjectMapper objectMapper) {
        this.templateInstancePlayer = templateInstancePlayer;
        this.templateInstanceService = templateInstanceService;
        this.namespaceApi = namespaceApi;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = CommonConstant.MQ_QUEUE_SECURITY_USER_DELETE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = CommonConstant.MQ_EXCHANGE_FANOUT_SECURITY_MODULE)
                    )
            }
    )
    public void userDeleted(String message) {

        Message<UserNamespacePair> messageBody = convertUserMessageBody(message);
        UserNamespacePair pair = messageBody.getData();
        Log.info(ApplicationRabbitMQMessageSubscriber.class.getName(),
                String.format("[ApplicationRabbitMQMessageSubscriber] received [%s] user deleted message", pair.getUsername()));
        List<TemplateInstance> templateInstances = templateInstanceService.findAll(pair.getUsername());
        try {
            for (TemplateInstance template : templateInstances) {
                templateInstancePlayer.delete(template.getId());
            }
            Log.info(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] [%s] user %s instance template has been deleted", pair.getUsername(), templateInstances.size()));
        } catch (Exception e) {
            Log.error(ApplicationRabbitMQMessageSubscriber.class.getName(),
                    String.format("[ApplicationRabbitMQMessageSubscriber] delete instance template error. %s", e.getMessage()));
        }

        try {
            FileHelper.deleteRecursively(Path.of(ROOT_PATH, pair.getNamespace()));
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

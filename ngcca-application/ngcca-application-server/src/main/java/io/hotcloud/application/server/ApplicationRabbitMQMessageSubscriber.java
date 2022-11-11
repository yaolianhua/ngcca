package io.hotcloud.application.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.api.core.files.FileHelper;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Log;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.security.api.user.UserNamespacePair;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

import static io.hotcloud.common.model.CommonConstant.ROOT_PATH;
@Component
@RequiredArgsConstructor
public class ApplicationRabbitMQMessageSubscriber {

    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateInstanceService templateInstanceService;

    private final NamespaceClient namespaceApi;
    private final ObjectMapper objectMapper;

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
            throw new NGCCACommonException(e.getMessage());
        }
    }
}

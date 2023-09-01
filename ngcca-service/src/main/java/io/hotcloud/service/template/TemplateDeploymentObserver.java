package io.hotcloud.service.template;

import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.message.MessageObserver;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TemplateDeploymentObserver implements MessageObserver {
    private final TemplateInstanceService templateInstanceService;
    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateDeploymentWatchService templateDeploymentWatchService;

    @Override
    public void onMessage(Message<?> message) {
        if (message.getData() instanceof WatchMessageBody messageBody) {
            subscribe(messageBody);
        }
    }

    public void subscribe(WatchMessageBody messageBody) {

        try {
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
                Log.info(this, null, String.format("received [%s] template delete events: %s/%s/%s", template.getName(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                templateInstancePlayer.delete(template.getId());
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())) {
                if (template.isSuccess()) {
                    return;
                }
                Log.info(this, null, String.format("received [%s] template events: %s/%s/%s", template.getName(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                templateDeploymentWatchService.watch(template);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())) {
                Log.info(this, null, String.format("received error events: %s/%s/%s", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
            }
        } catch (Exception e) {
            Log.error(this, null, Event.EXCEPTION, "template deployment observer error: " + e.getMessage());
        }

    }

}

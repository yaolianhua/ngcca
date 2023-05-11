package io.hotcloud.server.application.core;

import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstancePlayer;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import io.hotcloud.server.message.MessageObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationDeploymentObserver implements MessageObserver {
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationInstancePlayer applicationInstancePlayer;
    private final ApplicationDeploymentWatchService applicationDeploymentWatchService;

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
            ApplicationInstance fetched = applicationInstanceService.findOne(businessId);
            if (Objects.isNull(fetched)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())) {
                Log.info(this, null, String.format("Application Delete events: %s/%s/%s", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                applicationInstancePlayer.delete(businessId);
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())) {
                if (fetched.isSuccess()) {
                    return;
                }
                if (fetched.isDeleted()) {
                    Log.warn(this, null, String.format("[%s] user's application instance [%s] has been deleted", fetched.getUser(), fetched.getName()));
                    return;
                }
                Log.info(this, null, String.format("Application [%s] %s events: %s/%s/%s", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                applicationDeploymentWatchService.watch(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())) {
                Log.info(this, null, String.format("Application error events: %s/%s/%s", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
            }
        } catch (Exception e) {
            Log.error(this, null, e.getMessage());
        }

    }

}

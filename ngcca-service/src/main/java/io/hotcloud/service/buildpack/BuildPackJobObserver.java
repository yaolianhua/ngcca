package io.hotcloud.service.buildpack;

import io.fabric8.kubernetes.client.Watcher;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.service.message.MessageObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildPackJobObserver implements MessageObserver {
    private final BuildPackService buildPackService;
    private final BuildPackJobWatchService buildPackJobWatchService;

    @Override
    public void onMessage(Message<?> message) {
        if (message.getData() instanceof WatchMessageBody messageBody) {
            subscribe(messageBody);
        }
    }

    public void subscribe(WatchMessageBody messageBody) {

        try {
            if (!Objects.equals(WorkloadsType.Job.name(), messageBody.getKind())) {
                return;
            }

            String businessId = messageBody.getLabels().get(CommonConstant.K8S_APP_BUSINESS_DATA_ID);
            if (!StringUtils.hasText(businessId)) {
                return;
            }
            BuildPack fetched = buildPackService.findByUuid(businessId);
            if (Objects.isNull(fetched)) {
                return;
            }

            if (Objects.equals(Watcher.Action.DELETED.name(), messageBody.getAction())) {
                Log.info(this, null, String.format("BuildPack Delete events: %s/%s/%s", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                //ignore
            }

            if (Objects.equals(Watcher.Action.ADDED.name(), messageBody.getAction()) ||
                    Objects.equals(Watcher.Action.MODIFIED.name(), messageBody.getAction())) {
                if (fetched.isDone() || fetched.isDeleted()) {
                    return;
                }
                Log.info(this, null, String.format("BuildPack [%s] %s events: %s/%s/%s", businessId, messageBody.getAction(), messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
                buildPackJobWatchService.watch(fetched);
            }

            if (Objects.equals(Watcher.Action.ERROR.name(), messageBody.getAction())) {
                Log.info(this, null, String.format("BuildPack error events: %s/%s/%s", messageBody.getNamespace(), messageBody.getAction(), messageBody.getName()));
            }
        } catch (Exception e) {
            Log.error(this, null, e.getMessage());
        }

    }

}

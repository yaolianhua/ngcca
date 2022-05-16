package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedCreateEvent;
import io.hotcloud.buildpack.api.clone.GitClonedDeleteEvent;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.common.activity.ActivityAction;
import io.hotcloud.common.activity.ActivityLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class GitClonedListener {

    private final GitClonedService gitClonedService;
    private final GitClonedActivityLogger activityLogger;

    public GitClonedListener(GitClonedService gitClonedService,
                             GitClonedActivityLogger activityLogger) {
        this.gitClonedService = gitClonedService;
        this.activityLogger = activityLogger;
    }

    @EventListener
    public void cloned(GitClonedCreateEvent event) {
        try {
            GitCloned gitCloned = gitClonedService.saveOrUpdate(event.getGitCloned());
            log.info("[GitClonedListener] save or update git repository '{}'", gitCloned.getId());

            ActivityLog activityLog = activityLogger.log(ActivityAction.Create, gitCloned);
            log.info("[GitClonedListener] activity [{}] saved", activityLog.getId());
        } catch (Exception e) {
            log.error("[GitClonedListener] error. {}", e.getCause().getMessage());
        }
    }

    @EventListener
    public void deleted(GitClonedDeleteEvent event) {
        GitCloned gitCloned = event.getGitCloned();
        try {
            log.info("[GitClonedListener] git repository '{}' deleted", gitCloned.getId());

            ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, gitCloned);
            log.info("[GitClonedListener] activity [{}] saved", activityLog.getId());
        } catch (Exception e) {
            log.error("[GitClonedListener] error. {}", e.getCause().getMessage());
        }
    }
}

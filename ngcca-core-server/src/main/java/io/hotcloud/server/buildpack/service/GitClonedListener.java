package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.ActivityAction;
import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.utils.Log;
import io.hotcloud.vendor.buildpack.GitCloned;
import io.hotcloud.vendor.buildpack.GitClonedCreateEvent;
import io.hotcloud.vendor.buildpack.GitClonedDeleteEvent;
import io.hotcloud.vendor.buildpack.GitClonedService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
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
            Log.info(GitClonedListener.class.getName(),
                    GitClonedCreateEvent.class.getSimpleName(),
                    String.format("save or update git repository '%s'", gitCloned.getId()));
            ActivityLog activityLog = activityLogger.log(ActivityAction.Create, gitCloned);

        } catch (Exception e) {
            Log.error(GitClonedListener.class.getName(),
                    GitClonedCreateEvent.class.getSimpleName(),
                    String.format("%s", e.getCause().getMessage()));
        }
    }

    @EventListener
    public void deleted(GitClonedDeleteEvent event) {
        GitCloned gitCloned = event.getGitCloned();
        try {
            Log.info(GitClonedListener.class.getName(),
                    GitClonedDeleteEvent.class.getSimpleName(),
                    String.format("git repository '%s' deleted", gitCloned.getId()));
            ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, gitCloned);

        } catch (Exception e) {
            Log.error(GitClonedListener.class.getName(),
                    GitClonedDeleteEvent.class.getSimpleName(),
                    String.format("%s", e.getCause().getMessage()));
        }
    }
}

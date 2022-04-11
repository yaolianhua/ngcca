package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedEvent;
import io.hotcloud.buildpack.api.clone.GitClonedService;
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

    public GitClonedListener(GitClonedService gitClonedService) {
        this.gitClonedService = gitClonedService;
    }

    @EventListener
    public void cloned(GitClonedEvent event) {
        try {
            GitCloned gitCloned = gitClonedService.saveOrUpdate(event.getGitCloned());
            log.info("GitClonedListener. save or update git cloned '{}'", gitCloned.getId());
        } catch (Exception e) {
            log.error("GitClonedListener error. {}", e.getCause().getMessage());
        }
    }
}

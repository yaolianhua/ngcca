package io.hotcloud.buildpack.server.buildpack;

import com.github.benmanes.caffeine.cache.Cache;
import io.hotcloud.buildpack.api.model.GitRepositoryCloned;
import io.hotcloud.buildpack.api.model.event.GitRepositoryClonedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class GitRepositoryClonedListener {

    private final Cache cache;

    public GitRepositoryClonedListener(Cache cache) {
        this.cache = cache;
    }

    @EventListener
    public void cloned(GitRepositoryClonedEvent event) {
        GitRepositoryCloned cloned = event.getRepositoryCloned();
        log.info("{}", cloned);
    }
}

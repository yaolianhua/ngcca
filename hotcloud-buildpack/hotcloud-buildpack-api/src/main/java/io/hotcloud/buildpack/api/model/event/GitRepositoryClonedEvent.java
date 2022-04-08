package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.GitRepositoryCloned;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public class GitRepositoryClonedEvent extends ApplicationEvent {

    public GitRepositoryClonedEvent(GitRepositoryCloned repositoryCloned) {
        super(repositoryCloned);
    }

    public GitRepositoryCloned getRepositoryCloned() {
        return ((GitRepositoryCloned) super.getSource());
    }
}

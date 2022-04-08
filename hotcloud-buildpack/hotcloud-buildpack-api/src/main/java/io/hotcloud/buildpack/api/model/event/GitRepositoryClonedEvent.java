package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.GitCloned;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public class GitRepositoryClonedEvent extends ApplicationEvent {

    public GitRepositoryClonedEvent(GitCloned repositoryCloned) {
        super(repositoryCloned);
    }

    public GitCloned getRepositoryCloned() {
        return ((GitCloned) super.getSource());
    }
}

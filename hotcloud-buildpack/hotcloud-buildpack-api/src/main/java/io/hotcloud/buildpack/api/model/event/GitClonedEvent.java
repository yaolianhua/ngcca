package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.GitCloned;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public class GitClonedEvent extends ApplicationEvent {

    public GitClonedEvent(GitCloned cloned) {
        super(cloned);
    }

    public GitCloned getGitCloned() {
        return ((GitCloned) super.getSource());
    }
}

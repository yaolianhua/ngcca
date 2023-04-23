package io.hotcloud.module.buildpack;

import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public class GitClonedCreateEvent extends ApplicationEvent {

    public GitClonedCreateEvent(GitCloned cloned) {
        super(cloned);
    }

    public GitCloned getGitCloned() {
        return ((GitCloned) super.getSource());
    }
}

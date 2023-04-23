package io.hotcloud.vendor.buildpack;

import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public class GitClonedDeleteEvent extends ApplicationEvent {

    public GitClonedDeleteEvent(GitCloned cloned) {
        super(cloned);
    }

    public GitCloned getGitCloned() {
        return ((GitCloned) super.getSource());
    }
}

package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class BuildPackEvent extends ApplicationEvent {

    public BuildPackEvent(BuildPack buildPack) {
        super(buildPack);
    }

    public BuildPack getBuildPack() {
        return (BuildPack) super.getSource();
    }
}

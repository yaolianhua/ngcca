package io.hotcloud.module.buildpack.event;

import io.hotcloud.module.buildpack.BuildPack;
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

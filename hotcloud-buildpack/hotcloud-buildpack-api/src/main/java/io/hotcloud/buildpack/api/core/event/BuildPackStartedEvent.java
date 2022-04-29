package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackStartedEvent extends BuildPackEvent {

    public BuildPackStartedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

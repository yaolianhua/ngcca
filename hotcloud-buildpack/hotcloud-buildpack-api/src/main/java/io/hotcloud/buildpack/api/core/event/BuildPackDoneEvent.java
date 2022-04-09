package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackDoneEvent extends BuildPackEvent {

    public BuildPackDoneEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackDoneEvent extends BuildPackEvent {

    public BuildPackDoneEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackSuccessEvent extends BuildPackEvent {

    public BuildPackSuccessEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

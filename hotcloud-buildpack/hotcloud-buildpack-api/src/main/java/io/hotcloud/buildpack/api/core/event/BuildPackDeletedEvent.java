package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackDeletedEvent extends BuildPackEvent {

    public BuildPackDeletedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

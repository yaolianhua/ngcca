package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackStartedEventV2 extends BuildPackEvent {

    public BuildPackStartedEventV2(BuildPack buildPack) {
        super(buildPack);
    }
}

package io.hotcloud.module.buildpack.event;


import io.hotcloud.module.buildpack.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
public class BuildPackStartedEvent extends BuildPackEvent {

    public BuildPackStartedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

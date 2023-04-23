package io.hotcloud.vendor.buildpack.event;


import io.hotcloud.vendor.buildpack.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackStartedEventV2 extends BuildPackEvent {

    public BuildPackStartedEventV2(BuildPack buildPack) {
        super(buildPack);
    }
}

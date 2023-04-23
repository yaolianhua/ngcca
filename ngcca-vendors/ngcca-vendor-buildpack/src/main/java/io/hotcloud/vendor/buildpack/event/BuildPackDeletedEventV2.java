package io.hotcloud.vendor.buildpack.event;


import io.hotcloud.vendor.buildpack.BuildPack;

public class BuildPackDeletedEventV2 extends BuildPackEvent {

    public BuildPackDeletedEventV2(BuildPack buildPack) {
        super(buildPack);
    }
}

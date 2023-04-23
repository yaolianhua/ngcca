package io.hotcloud.module.buildpack.event;


import io.hotcloud.module.buildpack.BuildPack;

public class BuildPackDeletedEventV2 extends BuildPackEvent {

    public BuildPackDeletedEventV2(BuildPack buildPack) {
        super(buildPack);
    }
}

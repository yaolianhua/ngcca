package io.hotcloud.module.buildpack.event;


import io.hotcloud.module.buildpack.BuildPack;

public class BuildPackDeletedEvent extends BuildPackEvent {

    public BuildPackDeletedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

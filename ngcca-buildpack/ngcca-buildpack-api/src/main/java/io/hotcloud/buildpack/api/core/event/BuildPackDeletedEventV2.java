package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

public class BuildPackDeletedEventV2 extends BuildPackEvent {

    public BuildPackDeletedEventV2(BuildPack buildPack) {
        super(buildPack);
    }
}

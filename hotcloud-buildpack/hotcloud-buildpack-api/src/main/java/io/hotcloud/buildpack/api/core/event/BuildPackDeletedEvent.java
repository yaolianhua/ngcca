package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackDeletedEvent extends BuildPackEvent {

    private final boolean physically;

    public BuildPackDeletedEvent(BuildPack buildPack, boolean physically) {
        super(buildPack);
        this.physically = physically;
    }

    public boolean isPhysically() {
        return physically;
    }
}

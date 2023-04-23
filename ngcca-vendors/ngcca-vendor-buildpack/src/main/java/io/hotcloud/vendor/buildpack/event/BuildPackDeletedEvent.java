package io.hotcloud.vendor.buildpack.event;


import io.hotcloud.vendor.buildpack.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
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

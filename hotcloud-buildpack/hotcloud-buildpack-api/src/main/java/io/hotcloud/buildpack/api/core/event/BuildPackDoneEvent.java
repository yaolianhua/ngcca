package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
public class BuildPackDoneEvent extends BuildPackEvent {

    private final boolean success;

    public BuildPackDoneEvent(BuildPack buildPack, boolean success) {
        super(buildPack);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

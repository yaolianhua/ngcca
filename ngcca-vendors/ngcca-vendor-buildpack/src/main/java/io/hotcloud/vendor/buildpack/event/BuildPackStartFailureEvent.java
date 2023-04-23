package io.hotcloud.vendor.buildpack.event;

import io.hotcloud.vendor.buildpack.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
public class BuildPackStartFailureEvent extends BuildPackEvent {

    private final Throwable throwable;

    public BuildPackStartFailureEvent(BuildPack buildPack, Throwable throwable) {
        super(buildPack);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

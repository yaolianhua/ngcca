package io.hotcloud.buildpack.api.model.event;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
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

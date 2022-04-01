package io.hotcloud.buildpack.api.model;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackStartedEvent extends BuildPackEvent {

    public BuildPackStartedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

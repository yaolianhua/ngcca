package io.hotcloud.module.buildpack.event;


import io.hotcloud.module.buildpack.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
public class BuildPackArtifactUploadedEvent extends BuildPackEvent {

    public BuildPackArtifactUploadedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

package io.hotcloud.buildpack.api.core.event;

import io.hotcloud.buildpack.api.core.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildPackArtifactUploadedEvent extends BuildPackEvent {

    public BuildPackArtifactUploadedEvent(BuildPack buildPack) {
        super(buildPack);
    }
}

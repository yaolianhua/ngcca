package io.hotcloud.module.buildpack;

import io.hotcloud.module.buildpack.model.BuildImage;
import io.hotcloud.module.buildpack.model.BuildPack;

public interface BuildPackPlayer {

    /**
     * Deploy buildPack
     *
     * @param build {@link BuildImage}
     * @return {@link BuildPack}
     */
    BuildPack play(BuildImage build);

    /**
     * Delete buildPack resource
     *
     * @param id         buildPack ID
     * @param physically whether delete physically
     */
    void delete(String id, boolean physically);
}

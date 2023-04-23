package io.hotcloud.vendor.buildpack;

public interface BuildPackPlayerV2 {

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

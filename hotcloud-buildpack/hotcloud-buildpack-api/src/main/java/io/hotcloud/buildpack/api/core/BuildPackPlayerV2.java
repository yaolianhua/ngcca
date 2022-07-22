package io.hotcloud.buildpack.api.core;

public interface BuildPackPlayerV2 {

    /**
     * Deploy buildPack resource
     * * <p>this repository(branch) must contain a Dockerfile that can be built directly
     *
     * @param httpGitUrl Http git url
     * @param branch Git repository branch
     * @return {@link BuildPack}
     */
    BuildPack play(String httpGitUrl, String branch);

    /**
     * Delete buildPack resource
     *
     * @param id         buildPack ID
     * @param physically whether delete physically
     */
    void delete(String id, boolean physically);
}

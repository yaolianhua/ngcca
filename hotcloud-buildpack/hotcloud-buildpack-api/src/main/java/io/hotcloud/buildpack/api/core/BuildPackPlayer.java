package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackPlayer {

    /**
     * Generate {@link BuildPack} object
     *
     * @param gitUrl     Remote url of git repository
     * @param dockerfile dockerfile name. default name is {@code Dockerfile}
     * @param noPush     if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    BuildPack buildpack(String gitUrl, String dockerfile, Boolean noPush);

    /**
     * Apply BuildPack Yaml
     *
     * @param buildPack {@link BuildPack}
     */
    void apply(BuildPack buildPack);

    /**
     * Step with git clone
     *
     * @param gitUrl   remote git url. protocol supported http(s) only
     * @param branch   the initial branch to check out when cloning the repository.
     *                 Can be specified as ref name (<code>refs/heads/master</code>),
     *                 branch name (<code>master</code>) or tag name
     *                 (<code>v1.2.3</code>). The default is to use the branch
     *                 pointed to by the cloned repository's HEAD and can be
     *                 requested by passing {@code null} or <code>HEAD</code>.
     * @param username remote repository username credential
     * @param password remote repository password credential
     */
    void clone(String gitUrl, String branch, String username, String password);
}

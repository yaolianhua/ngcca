package io.hotcloud.buildpack.api;

import javax.annotation.Nullable;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface GitApi {

    /**
     * Clone git repository
     *
     * @param remote   remote git url. protocol supported https only
     * @param branch   the initial branch to check out when cloning the repository.
     *                 Can be specified as ref name (<code>refs/heads/master</code>),
     *                 branch name (<code>master</code>) or tag name
     *                 (<code>v1.2.3</code>). The default is to use the branch
     *                 pointed to by the cloned repository's HEAD and can be
     *                 requested by passing {@code null} or <code>HEAD</code>.
     * @param local    the path will be cloned locally
     * @param username remote repository username credential
     * @param password remote repository password credential
     * @return {@link  Boolean}
     */
    Boolean clone(String remote, @Nullable String branch, String local, @Nullable String username, @Nullable String password);
}

package io.hotcloud.buildpack.api.clone;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface GitClonedService {

    /**
     * Save or update with giving {@link GitCloned}
     *
     * @param cloned {@link GitCloned}
     */
    void saveOrUpdate(GitCloned cloned);

    /**
     * Find {@link GitCloned} with giving {@code  username} and {@code gitProject}
     *
     * @param username   user's username
     * @param gitProject cloned git project
     * @return {@link GitCloned}
     */
    GitCloned findOne(String username, String gitProject);

    /**
     * Delete {@link GitCloned} with giving {@code  username} and {@code gitProject}
     *
     * @param username   user's username
     * @param gitProject cloned git project
     */
    void deleteOne(String username, String gitProject);

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

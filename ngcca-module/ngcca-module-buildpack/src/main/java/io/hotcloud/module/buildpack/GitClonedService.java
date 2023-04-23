package io.hotcloud.module.buildpack;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface GitClonedService {

    /**
     * Save or update with giving {@link GitCloned}
     *
     * @param cloned {@link GitCloned}
     * @return {@link GitCloned}
     */
    GitCloned saveOrUpdate(GitCloned cloned);

    /**
     * Find {@link GitCloned} with giving {@code  user} and {@code gitProject}
     *
     * @param user       user's username
     * @param gitProject cloned git project
     * @return {@link GitCloned}
     */
    GitCloned findOne(String user, String gitProject);

    /**
     * Find {@link GitCloned} with giving {@code  clonedId}
     *
     * @param clonedId id
     * @return {@link GitCloned}
     */
    GitCloned findOne(String clonedId);

    /**
     * Find user's {@link GitCloned} with giving {@code user}
     *
     * @param user user
     * @return {@link GitCloned}
     */
    List<GitCloned> findAll(String user);

    /**
     * Find all {@link GitCloned}
     *
     * @return {@link GitCloned}
     */
    List<GitCloned> findAll();

    /**
     * Delete {@link GitCloned} with giving {@code  user} and {@code gitProject}
     *
     * @param user       user's username
     * @param gitProject cloned git project
     */
    void deleteOne(String user, String gitProject);

    /**
     * Delete all {@link GitCloned} with giving {@code  user}
     *
     * @param user user's username
     */
    void delete(String user);

    /**
     * Delete {@link GitCloned} with the giving {@code id}
     *
     * @param id git cloned id
     */
    void deleteById(String id);

    /**
     * Step with git clone
     *
     * @param gitUrl     remote git url. protocol supported http(s) only
     * @param dockerfile dockerfile name. default name is {@code Dockerfile}
     * @param branch     the initial branch to check out when cloning the repository.
     *                   Can be specified as ref name (<code>refs/heads/master</code>),
     *                   branch name (<code>master</code>) or tag name
     *                   (<code>v1.2.3</code>). The default is to use the branch
     *                   pointed to by the cloned repository's HEAD and can be
     *                   requested by passing {@code null} or <code>HEAD</code>.
     * @param username   remote repository username credential
     * @param password   remote repository password credential
     */
    void clone(String gitUrl, String dockerfile, String branch, String username, String password);
}

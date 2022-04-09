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
}

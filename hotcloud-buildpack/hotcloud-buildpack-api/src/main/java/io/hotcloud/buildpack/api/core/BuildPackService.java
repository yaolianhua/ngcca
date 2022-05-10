package io.hotcloud.buildpack.api.core;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackService {

    /**
     * Save or update {@link BuildPack} object
     *
     * @param buildPack {@link BuildPack}
     * @return Saved {@link BuildPack}
     */
    BuildPack saveOrUpdate(BuildPack buildPack);

    /**
     * Find all {@link BuildPack} with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll(String user, String clonedId);

    /**
     * Find all user's {@link BuildPack} with giving {@code user}
     *
     * @param user user's username
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll(String user);

    /**
     * Find all {@link BuildPack} with giving {@code clonedId}
     *
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    List<BuildPack> findByClonedId(String clonedId);

    /**
     * Find all user's {@link BuildPack}
     *
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll();

    /**
     * Find one with giving buildPack id
     *
     * @param id buildPack id
     * @return {@link BuildPack}
     */
    BuildPack findOne(String id);

    /**
     * Find un-done {@link BuildPack} with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    BuildPack findOneOrNullWithNoDone(String user, String clonedId);

    /**
     * Delete all buildPack physically
     */
    void deleteAll();

    /**
     * Delete all user's buildPack physically with the giving {@code user}
     *
     * @param user user's username
     */
    void deleteAll(String user);

    /**
     * Delete buildPack with giving id
     *
     * @param id         buildPack ID
     * @param physically Whether to physically delete
     */
    void delete(String id, boolean physically);
}

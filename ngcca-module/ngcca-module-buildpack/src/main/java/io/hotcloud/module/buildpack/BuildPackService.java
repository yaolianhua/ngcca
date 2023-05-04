package io.hotcloud.module.buildpack;

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
     * Find all user's {@link BuildPack} with giving {@code user}
     *
     * @param user user's username
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll(String user);

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
     * Find one with giving buildPack business id
     *
     * @param uuid buildPack business id
     * @return {@link BuildPack}
     */
    BuildPack findByUuid(String uuid);

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

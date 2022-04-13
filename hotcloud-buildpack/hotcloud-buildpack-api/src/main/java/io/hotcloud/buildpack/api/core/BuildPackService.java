package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

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
     * Find all with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll(String user, String clonedId);

    /**
     * Find one with giving buildPack id
     *
     * @param id buildPack id
     * @return {@link BuildPack}
     */
    BuildPack findOne(String id);

    /**
     * Find un-done BuildPack with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    BuildPack findOneOrNullWithNoDone(String user, String clonedId);

    /**
     * Delete all
     */
    void deleteAll();
}

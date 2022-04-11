package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackService {

    /**
     * Save {@link BuildPack} object
     *
     * @param buildPack {@link BuildPack}
     * @return Saved {@link BuildPack}
     */
    BuildPack save(BuildPack buildPack);

    /**
     * Find all with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    List<BuildPack> findAll(String user, String clonedId);

    /**
     * Find one the value {@code done} is false with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPack}
     */
    BuildPack findOneWithNoDone(String user, String clonedId);

    /**
     * Delete all
     */
    void deleteAll();
}

package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApiAdaptor {

    /**
     * Adapter for {@link BuildPackApi}
     *
     * @see BuildPackApi#buildpack(String, String, String, boolean, String, String, String, Map)
     */
    BuildPack buildpack(String gitUrl, String dockerfile, boolean force, String registry, String registryProject, String registryUser, String registryPass);
}

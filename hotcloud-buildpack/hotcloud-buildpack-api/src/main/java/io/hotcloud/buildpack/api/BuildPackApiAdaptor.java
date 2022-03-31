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
     * @param gitUrl          Remote url of git repository
     * @param dockerfile      dockerfile name. default name is {@code Dockerfile}
     * @param force           Whether to force cloning, if the specified path is not empty, it will be forcibly deleted and then cloned
     * @param registry        The registry address where the {@code git project} build is pushed to
     * @param registryProject In which project will be pushed in the registry
     * @param registryUser    The registry auth user if it's non-public
     * @param registryPass    The registry auth password if it's non-public
     * @return {@link BuildPack}
     * @see BuildPackApi#buildpack(String, String, String, boolean, String, String, String, Map)
     */
    BuildPack buildpack(String gitUrl, String dockerfile, boolean force, String registry, String registryProject, String registryUser, String registryPass);
}

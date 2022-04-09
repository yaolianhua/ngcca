package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
interface BuildPackApi {

    /**
     * Generate {@link BuildPack} object
     *
     * @param namespace    In which namespace the {@link BuildPack} resource be created
     * @param gitProject   Project name of git cloned
     * @param registry     The registry address where the {@code git project} build is pushed to
     * @param registryUser The registry auth user if it's non-public
     * @param registryPass The registry auth password if it's non-public
     * @param kanikoArgs   Kaniko args
     * @return {@link BuildPack}
     */
    BuildPack buildpack(String namespace,
                        String gitProject,
                        String registry,
                        String registryUser,
                        String registryPass,
                        Map<String, String> kanikoArgs);
}

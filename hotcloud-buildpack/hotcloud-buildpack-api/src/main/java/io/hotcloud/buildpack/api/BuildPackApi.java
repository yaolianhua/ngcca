package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
interface BuildPackApi {

    /**
     * @param namespace    In which namespace the {@link BuildPack} resource be created
     * @param gitUrl       Remote url of git repository
     * @param clonePath    The path will be cloned locally
     * @param force        Whether to force cloning, if the specified path is not empty, it will be forcibly deleted and then cloned
     * @param async        Whether to execute the clone repository asynchronously. default is {@code false}
     * @param registry     The registry address where the {@code git project} build is pushed to
     * @param registryUser The registry auth user if it's non-public
     * @param registryPass The registry auth password if it's non-public
     * @param kanikoArgs   Kaniko args
     * @return {@link BuildPack}
     */
    BuildPack buildpack(String namespace,
                        String gitUrl,
                        String clonePath,
                        boolean force,
                        boolean async,
                        String registry,
                        String registryUser,
                        String registryPass,
                        Map<String, String> kanikoArgs);
}

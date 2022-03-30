package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
interface BuildPackApi {

    BuildPack buildpack(String namespace, String gitUrl, String clonePath, boolean force, String registry, String registryUser, String registryPass, Map<String, String> kanikoArgs);
}

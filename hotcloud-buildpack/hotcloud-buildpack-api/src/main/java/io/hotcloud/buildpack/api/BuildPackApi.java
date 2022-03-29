package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApi {

    BuildPack buildpack(String namespace, String gitUrl, String local, boolean force, String registry, String registryUser, String registryPass, Map<String, String> kanikoArgs);
}

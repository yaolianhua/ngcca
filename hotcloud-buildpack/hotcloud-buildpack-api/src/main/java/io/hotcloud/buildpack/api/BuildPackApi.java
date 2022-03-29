package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApi {

    BuildPack buildpack(String namespace, String gitUrl, String local, String registry, String registryUser, String registryPass);
}

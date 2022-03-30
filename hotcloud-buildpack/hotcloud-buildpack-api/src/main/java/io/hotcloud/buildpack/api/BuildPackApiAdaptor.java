package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApiAdaptor {

    BuildPack buildpack(String gitUrl, String dockerfile, boolean force, String registry, String registryProject, String registryUser, String registryPass);
}
